此文件的内容受 GNU 通用公共许可证 (GPL) 第 2 版或更高版本（“许可证”）的约束；除非遵守许可，否则您不得使用此文件。您可以在http://www.gnu.org/copyleft/gpl.html获得许可证的副本。

根据许可分发的软件按“原样”分发，不提供任何类型的明示或暗示保证。请参阅许可证以了解管理许可证下的权利和限制的特定语言。

该文件最初是作为支持 Nisan 和 Schocken 合着的“计算系统元素”一书，麻省理工学院出版社 2005 年的软件套件的一部分开发的。如果您修改了该文件的内容，请记录并清楚地标记您的更改，以便他人的利益。


内容
--------

1. 目录结构及编译说明
2. 用于在 Java 中实现芯片的芯片 API
3. 用于在 Java 中实现 VM 函数/类的 VMCode API


目录结构及编译说明:
-------------------------------------------------

代码结构为多模块 maven 项目。从根目录编译运行

```bash
    mvn install
```


* InstallDir - 包含一些资源（如图标和默认脚本），以及 shell 和 bat 可执行脚本。

用于在 Java 中实现芯片的芯片 API
------------------------------------------------

Nand2Tetris 软件套件允许在 Java 中实现新芯片，通过芯片 API 与硬件模拟器一起使用，下文将对此进行描述。此功能既可以实现更高的仿真速度，又可以在不公开其实现的情况下为学生提供工作芯片。

强烈建议在实现芯片之前浏览 Nand2Tetris 软件套件提供的芯片实现。这些芯片的 HDL 文件位于 InstallDir/builtInChips 目录下，这些芯片的 Java 源代码位于 BuiltInChipsSource 目录下。

每个芯片的输入和输出引脚由芯片的 HDL 文件指定，该文件应包含芯片名称，后跟 .hdl 后缀，并位于 InstallDir/builtInChips 目录中。HDL 应该像普通的 HDL 文件一样形成（参见http://www.nand2tetris.org 上提供的硬件模拟器教程），并以通常的方式指定芯片名称和所有输入和输出引脚。代替通常包含实现的门列表，该行：

```
    BUILTIN ChipName;
```    

应出现（其中 ChipName 应替换为芯片的名称）。

除了芯片的 HDL 文件外，每个芯片都应在与芯片名称相同的单独类中实现。编译后的实现应该位于 CLASSPATH 中某个地方的 builtInChips 包中（例如，与 Nand2Tetris 软件套件一起提供的上述芯片的编译对应物位于 InstallDir/builtInChips 目录下）。

芯片类应该（直接或间接）扩展 Hack.Gates.BuiltInGate 类，并且可以覆盖以下三种方法中的任何一种（默认情况下什么都不做）：

```java
/** 在时钟上升时调用（对时钟芯片有用） */
void clockUp();

/** 当时钟下降时调用（对时钟芯片有用） */
void clockDown();

/** 每当任何输入引脚发生变化时调用（对组合芯片有用） */
void reCompute(); 
```

所需的初始化代码可以放置在不接受参数的构造函数中，并且可以定义任意数量的数据成员。

芯片代码可以分别通过数据成员inputPins和访问输入和输出引脚 outputPins。

可以通过评估调用inputPins[n].get().

在芯片 HDL 文件中声明为第 n 个（从零开始）的 b 位输出引脚/总线的值可以通过调用outputPins[n].set(v).

所述Nand2Tetris软件套件硬件仿真器芯片的Java API还提供用于实现芯片的GUI可视化（类似于一个由提供的实现支持ALU，RAM*，ROM32K，ARegister和DRegister芯片）。实现芯片应该扩展Hack.Gates.BuilInGateWithGUI类。有关SimulatorsPackage/Hack/Games/BuiltInGateWithGUI.java应由 gui 驱动的芯片实现的其他方法的示例和信息，请参阅此类（位于 ）和上述 gui 驱动的芯片的实现。


用于在 Java 中实现 VM 函数/类的 VMCode API
-----------------------------------------------------------------

Nand2Tetris 软件套件允许在 Java 中实现新的 VM 功能，供 VM 模拟器通过 VMCode API 使用，下文将对此进行描述。此功能允许实现更高的模拟速度和允许 VM 程序访问在 Hack 平台上不可用或不可行的功能的能力（此类功能可能包括例如时间和日期查询、随机数生成使用RNG 守护进程并使用第 3 方封闭库执行复杂计算）。

每当 VM Emulator 遇到对带有当前程序不包含 *.vm 实现的前缀的 VM 函数的调用（例如Screen.drawPixel，在当前程序不包含名为 的文件时调用 Screen.vm），VM Emulator 将如果存在这样的实现，则调用该 VM 函数的 Java 实现。这种优先级机制类似于硬件模拟器中芯片 API 的优先级机制。由于本书中没有讨论这种机制，因此在加载需要这种用法的程序时，将弹出一个对话框以确认 VM 函数的 Java 实现的用法。

强烈建议在实现新的 VM 功能之前浏览 Nand2Tetris 软件套件中提供的 Jack OS 的实现。此实现的 Java 源代码位于 BuiltInVMCode 目录下。

需要注意的是，由于VMCode API 允许在VM 级别而不是Jack 级别实现函数，因此该API 中没有构造函数或方法的概念，而只有函数的概念。函数在概念上可能是类方法，因此接收一个 this-style 指针作为第一个参数（例如，就像String.*函数一样），或者在概念上可能是类构造函数，它返回一个它们分配和初始化的 this-style 指针（例如，就像String.new函数一样） ) 但在概念上可能仅仅是函数或静态方法（例如，就像Output.*函数一样）。

每个 VM 功能都由单个 Java 静态方法实现。由于所有 VM 数据都是 16 位数量，而 Java shorts 是 16 位数量，因此静态方法的所有参数都应该是 Java shorts。为方便起见，静态方法可能会返回void（ 的值0将返回给调用函数）、boolean（false将转换为0、trueto 0xffff）、 char（将转换为short并返回）或short。

与普通.vm文件一样（参见www.nand2tetris.org 上的 VM 模拟器教程），所有以相同前缀开头的 VM 函数（例如，所有实现同一 Jack 类的方法、构造函数和函数的 VM 函数）都一起实现。在普通.vm文件的情况下，所有此类函数都在单个文件中实现，该文件以前缀（例如，类名）作为其名称，因此所有此类函数共享相同的静态段。在 Java 实现的 VM 函数的情况下，所有这些函数都由单个类的静态方法实现，将前缀作为其名称，因此可以使用类的静态变量共享数据。

编译后的类应（直接或间接）扩展 Hack.VMEmulator.BuiltInVMClass该类，并应驻留在builtInVMCode 包中某处CLASSPATH（例如，实现 Nand2Tetris 软件套件中提供的 Jack OS 的已编译类位于 InstallDir/builtInVMCode 目录下）。

实现 VM 功能的 Java 静态方法可以使用它继承自的以下任何静态方法与虚拟机通信
`Hack.VMEmulator.BuiltInVMClass`:

```java
/**
 * 返回存储在 VM 贮中给定地址处的值（地址
 * 参数是一个 int 而不是为了方便起见的缩写，但可能只在 HEAP_START_ADDRESS - HEAP_END_ADDRESS 或
 * SCREEN_START_ADDRESS - SCREEN_END_ADDRESS (这些作为静态最终
 * Hack.VMEmulator.BuiltInVMClass 的常量提供
 */
short readMemory(int address);

/**
 * 更改存储在 VM 贮中给定地址处的值（该值被
* 强制转换为 short 并且地址必须是合法的 - 见上文）。如果数据流
* 动画打开，VM 贮中的变化将被动画化。
 */
void writeMemory(int address, int value);

/**
 * 使用给定的参数 调用指定的 VM 函数（可以在普通的 .vm 文件中实现，也可以在 Java 中使用 VMCode API 实现）。返回函数
的 return 。为了实现最大的模块化，这个函数应该用于所有调用当前类未实现的 VM 函数，以确保 VM 模拟器当前使用的实现（它可能会或可能不会在 Java 中实现）是叫。
 */
short callFunction(String functionName, short[] params);

/**
 * 为方便起见，提供了 callFunction 方法的版本，用于调用接受 0-4 个参数的VM 函数，而无需分配参数数组。
 */
short callFunction(String functionName, short param1, ...); 

/**
 * 用于停止VM程序（这是
Jack OS的Sys.halt函数的
Java实现调用的函数）。如果提供了可选消息*（非空），则会打开一个弹出窗口以显示它。
* 
* 重要提示：实现 VM 函数的 Java 静态方法不应进入
* 阻塞无限循环
 */
void infiniteLoop(String message);
```

调用上述任何静态方法的函数必须声明为 throw Hack.VMEmulator.TerminateVMProgramThrowable。Throwable如果用户决定重新启动 VM 程序（例如通过<<按钮），任何上述静态方法都会抛出此类的实例 。这个 throwable 可能会被调用方法捕获，但必须重新抛出。

为方便起见，以下常量由Hack.VMEmulator.BuiltInVMClass扩展它的类提供 ：

* `short SCREEN_START_ADDRESS`
* `short SCREEN_END_ADDRESS`
* `int SCREEN_WIDTH`
* `int SCREEN_HEIGHT`
* `short HEAP_START_ADDRESS`
* `short HEAP_END_ADDRESS`
* `short KEYBOARD_ADDRESS`
* `short NEWLINE_KEY`
* `short BACKSPACE_KEY`

最后，应该注意的是，Java 语言不允许声明被调用的方法new（例如 来自 Jack OS的String.new和Array.new构造函数）。使用 VMCode API 实现此类 VM 函数是通过实现称为NEW （全部大写）的静态 Java 方法来实现的- VM 模拟器将NEW在收到调用该new函数的请求时调用该函数。

全文由谷歌翻译自动完成。