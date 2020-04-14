package Mk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Motyak
 */
public class TextFile {
	
	public static void main(String[] args) throws Exception {
		String userDir = System.getProperty("user.dir") + File.separator;
		String content = new String("line1\nline2\nline3");
		
		System.out.println("creating file1 with the content of str..");
		TextFile.stringToFile(content, userDir + "file1");
		
		System.out.println("renaming file1 into file2.. (and overwriting if the file already exists)");
		TextFile.rename(userDir, "file1", "file2", true);
		
		System.out.println("reading and printing file2 content..");
		System.out.println(TextFile.fileToString(userDir + "file2"));
		
		System.out.println("reading only the first and last line of file2..");
		System.out.println(TextFile.overview(userDir + "file2", 1));
		
		System.out.println("deleting file2..");
		new File(userDir + "file2").delete();
	}

	/**
	 * Read a file and return its content as a String
	 * @param filePath the absolute path of the targeted file
	 * @return the content of the targeted file as a String
	 * @throws IOException if there's a problem finding/reading the file
	 */
	public static String fileToString(String filePath) throws IOException {
		InputStream is = new FileInputStream(filePath);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();

		while(line != null) {
		   sb.append(line).append("\n");
		   line = buf.readLine();
		}
		
		buf.close();
		sb.setLength(sb.length() - 1);	//Gets rid of the last line break to keep the exact same content
		
		return sb.toString();
	}
	
	/**
	 * Create a file and write a String in it
	 * @param stringable the content to write into the file, will use 'toString()' implementation
	 * @param filePath the absolute path of the file to create
	 * @throws IOException if there's a problem finding/writing the file
	 */
	public static void stringToFile(Object stringable, String filePath) throws IOException {
		String str = stringable.toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(str);
	    writer.close();
	}
	
	/**
	 * Read a file and return an overview of it
	 * @param filePath the absolute path of the targeted file
	 * @param nbOfLines the nb of first and last lines to return
	 * @return an overview of the targeted file (certain number of lines)
	 * @throws IOException if there's a problem finding/reading the file
	 */
	public static String overview(String filePath, int nbOfLines) throws IOException {
		long totalNbOfLines = TextFile.nbOfLines(filePath);
		
		if(totalNbOfLines <= 2*nbOfLines)
			return TextFile.fileToString(filePath);
		
		InputStream is = new FileInputStream(filePath);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		
		LineNumberReader reader = new LineNumberReader(new FileReader(filePath));
		for(int i = 1 ; i <= nbOfLines ; ++i)
			sb.append(buf.readLine()).append("\n");
		sb.append("...\n");
		reader.skip(totalNbOfLines - nbOfLines);
		for(int i = 1 ; i <= nbOfLines ; ++i)
			sb.append(buf.readLine()).append("\n");
		
		buf.close();
		reader.close();
		sb.setLength(sb.length() - 1);
		
		return sb.toString();
	}
	
	/**
	 * Rename an existing file
	 * @param dirPath the directory containing the file to rename
	 * @param oldFileName the name of the file to rename
	 * @param newFileName the future name of the file
	 * @param overwrite indicates if you wish to overwrite if there's already a file with the name you want
	 * @throws Exception if there's a problem finding/renaming the file
	 */
	public static void rename(String dirPath, String fileName, String newFileName, boolean overwrite) throws Exception {
		Path source = Paths.get(dirPath + fileName);
		File file=new File(dirPath + newFileName);
		if(file.exists()) {
			if(overwrite)
				file.delete();
			else
				throw new Exception("Cannot rename : the name of the file already exists.");
		}
		Files.move(source,source.resolveSibling(newFileName));
	}
	
	private static long nbOfLines(String filePath) throws IOException {
		return Files.lines(Paths.get(filePath)).count();
	}
}
