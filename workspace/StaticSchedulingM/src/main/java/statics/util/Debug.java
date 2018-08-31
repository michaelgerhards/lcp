package statics.util;

import java.io.PrintStream;

public class Debug {

    public static Debug INSTANCE = new Debug();

    private int debug = -1;
    private PrintStream out = new PrintStream(System.out, true);

    private Debug() {
        // private
    }

    public void close() {
        out.close();
    }

    public void flush() {
        out.flush();
    }

    public int getDebug() {
        return debug;
    }

    public PrintStream getPrintStream() {
        return out;
    }

    public void print(int shouldPrint, Object... contents) {
        if (debug >= shouldPrint) {
            for (Object ob : contents) {
                getPrintStream().print(ob.toString());
            }
        }
    }

    public void aPrint(Object... contents) {
        for (Object ob : contents) {
            getPrintStream().print(ob.toString());
        }
    }

    public void printf(int shouldPrint, String content, Object... arg1) {
        if (debug >= shouldPrint) {
            getPrintStream().printf(content, arg1);
        }
    }

    public void aPrintf(String string, Object... i) {
        out.printf(string, i);

    }

    public void aPrintln() {
        getPrintStream().println();
    }

    public void aPrintln(boolean string) {
        getPrintStream().println(string);
    }

    public void println(int shouldPrint, Object... contents) {
        if (debug >= shouldPrint) {
            for (Object ob : contents) {
                getPrintStream().print(ob.toString());
            }
            getPrintStream().println();
        }
    }

    public void aPrintln(Object... contents) {
        for (Object ob : contents) {
            getPrintStream().print(ob.toString());
        }
        getPrintStream().println();
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public void setPrintStream(PrintStream out2) {
        this.out = out2;
    }
}
