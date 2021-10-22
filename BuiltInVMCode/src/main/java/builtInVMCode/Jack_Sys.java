/********************************************************************************
 * The contents of this file are subject to the GNU General Public License      *
 * (GPL) Version 2 or later (the "License"); you may not use this file except   *
 * in compliance with the License. You may obtain a copy of the License at      *
 * http://www.gnu.org/copyleft/gpl.html                                         *
 *                                                                              *
 * Software distributed under the License is distributed on an "AS IS" basis,   *
 * without warranty of any kind, either expressed or implied. See the License   *
 * for the specific language governing rights and limitations under the         *
 * License.                                                                     *
 *                                                                              *
 * This file was originally developed as part of the software suite that        *
 * supports the book "The Elements of Computing Systems" by Nisan and Schocken, *
 * MIT Press 2005. If you modify the contents of this file, please document and *
 * mark your changes clearly, for the benefit of others.                        *
 ********************************************************************************/

package builtInVMCode;

import Hack.VMEmulator.TerminateVMProgramThrowable;

/**
 * A built-in implementation for the Sys class of the Jack OS.
 */

@SuppressWarnings("UnusedDeclaration")
public class Jack_Sys extends JackOSClass {

    public static void init() throws TerminateVMProgramThrowable {
        callFunction("Memory.init");
        callFunction("Math.init");
        callFunction("Screen.init");
        callFunction("Output.init");
        callFunction("Keyboard.init");
        callFunction("Main.main");
        infiniteLoop("Program Halted: Main.main finished execution");
    }

    public static void halt() throws TerminateVMProgramThrowable {
        infiniteLoop("Program Halted");
    }

    public static void wait(short duration) throws TerminateVMProgramThrowable {
        if (duration < 0)
            error(SYS_WAIT_NEGATIVE_DURATION);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void error(short errorCode) throws TerminateVMProgramThrowable {
        callFunction("Output.printString", javaStringToJackStringUsingVM("ERR"));
        callFunction("Output.printInt", errorCode);

        String errorDescription = null;
        switch (errorCode) {
            case SYS_WAIT_NEGATIVE_DURATION:
                errorDescription = "续时必为正";
                break;
            case ARRAY_NEW_NONPOSITIVE_SIZE:
                errorDescription = "队夵必为正";
                break;
            case MATH_DIVIDE_ZERO:
                errorDescription = "除以零";
                break;
            case MATH_SQRT_NEGATIVE:
                errorDescription = "负数不能开平方根";
                break;
            case MEMORY_ALLOC_NONPOSITIVE_SIZE:
                errorDescription = "配存夵必为正";
                break;
            case MEMORY_ALLOC_HEAP_OVERFLOW:
                errorDescription = "堆溢";
                break;
            case SCREEN_DRAWPIXEL_ILLEGAL_COORDS:
                errorDescription = "非法像素座标";
                break;
            case SCREEN_DRAWLINE_ILLEGAL_COORDS:
                errorDescription = "非法行座标";
                break;
            case SCREEN_DRAWRECTANGLE_ILLEGAL_COORDS:
                errorDescription = "非法矩座标";
                break;
            case SCREEN_DRAWCIRCLE_ILLEGAL_CENTER:
                errorDescription = "非法圆心座标";
                break;
            case SCREEN_DRAWCIRCLE_ILLEGAL_RADIUS:
                errorDescription = "非法半径";
                break;
            case STRING_NEW_NEGATIVE_LENGTH:
                errorDescription = "丄长必非负";
                break;
            case STRING_CHARAT_ILLEGAL_INDEX:
                errorDescription = "串号出界";
                break;
            case STRING_SETCHARAT_ILLEGAL_INDEX:
                errorDescription = "串号出界";
                break;
            case STRING_APPENDCHAR_FULL:
                errorDescription = "满串";
                break;
            case STRING_ERASELASTCHAR_EMPTY:
                errorDescription = "空串";
                break;
            case STRING_SETINT_INSUFFICIENT_CAPACITY:
                errorDescription = "串容量不足";
                break;
            case OUTPUT_MOVECURSOR_ILLEGAL_POSITION:
                errorDescription = "非法光标位";
                break;
        }
        infiniteLoop("程停了: " + errorDescription);
    }
}
