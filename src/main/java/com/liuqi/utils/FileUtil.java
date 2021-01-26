package com.liuqi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * 文件
 */
public class FileUtil {

	/**
	 * 
	 * @param engine
	 * @param ctx
	 * @param vmPath  vm路径
	 * @param outPath 输出路径
	 * @param outFileName  输出名称
	 * @throws Exception
	 */
	public static void outPutFile(VelocityEngine engine, VelocityContext ctx,
			String vmPath, String outPath, String outFileName) throws Exception {
		Template t = engine.getTemplate(vmPath);
		File file = new File(outPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		StringWriter writer = new StringWriter();
		t.merge(ctx, writer);
		FileOutputStream fos = new FileOutputStream(new File(outPath+"/"+outFileName));
		PrintStream ps = new PrintStream(fos, true, "UTF-8");// 这里我们就可以设置编码了
		ps.print(writer.toString());
		ps.flush();
		ps.close();
		fos.close();
	}
}
