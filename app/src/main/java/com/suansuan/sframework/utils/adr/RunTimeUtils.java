package com.suansuan.sframework.utils.adr;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 运行器的工具类
 */
@SuppressWarnings("all")
public class RunTimeUtils {

    public static int runScriptAsRoot(Context ctx, String script) {
        StringBuilder res = new StringBuilder();
        final File file = new File(ctx.getCacheDir(), "secopt.sh");
        final ScriptRunTask runner = new ScriptRunTask(file, script, res);
        runner.start();
        try {
            runner.join(40000);
            if (runner.isAlive()) {
                runner.interrupt();
                runner.join(150);
                runner.destroy();
                runner.join(50);
            }
        } catch (InterruptedException ex) {}
        return runner.exitcode;
    }

    private static final class ScriptRunTask extends Thread {

        private int exitcode = -1;
        private final File file;
        private final String script;
        private final StringBuilder res;
        private Process runtime;

        public ScriptRunTask (File file, String script, StringBuilder res) {
            this.file = file;
            this.script = script;
            this.res = res;
        }

        @Override
        public void run() {
            try{
                String absolutePath = createScriptFile();

                runtime = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(runtime.getOutputStream());
                os.writeBytes(absolutePath);
                os.flush();
                os.close();

                InputStreamReader inputStreamReader = new InputStreamReader(runtime.getInputStream());
                char[] buffer = new char[1024];
                int len;
                while ((len = inputStreamReader.read()) != -1){
                    if(res != null){
                        res.append(buffer, 0, len);
                    }
                }

                inputStreamReader = new InputStreamReader(runtime.getErrorStream());
                len = 0 ;
                while ((len = inputStreamReader.read()) != -1){
                    if(res != null){
                        res.append(buffer, 0, len);
                    }
                }

                if(runtime != null){
                    this.exitcode = runtime.waitFor();
                }
            } catch (IOException e) {
                if (res != null) {
                    res.append("\n" + e);
                }
            } catch (InterruptedException e) {
                if (res != null) {
                    res.append("\nOperation timed-out");
                }
            } finally {
                destroy();
            }
        }

        public synchronized void destroy(){
            if(runtime != null){
                runtime.destroy();
            }
            runtime = null;
        }

        private String createScriptFile() throws IOException, InterruptedException {
            this.file.createNewFile();
            String absolutePath = file.getAbsolutePath();
            Runtime.getRuntime().exec("chmod 777 " + absolutePath).waitFor();
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));
            if(new File("/system/bin/sh").exists()){
                out.write("#!/system/bin/sh\n");
            }
            out.write(script);
            if(!script.endsWith("\n")){
                out.write("\n");
            }
            out.write("exit\n");
            out.flush();
            out.close();
            return absolutePath;
        }
    }
}
