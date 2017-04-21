package reference2dsm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtil {
	private File inputF;
	private File outputF;
	private FileReader fr;
	private BufferedReader bfr;
	private FileWriter fw;
	private BufferedWriter bfw;
	
	public IOUtil() {
	}
	
	public boolean setInput(String inputFileName) {
		inputF = new File(inputFileName);
		if (inputF.exists()) {
			try {
				if (bfr!=null) {
					bfr.close();
				}
				if (fr!=null) {
					fr.close();
				}
				fr = new FileReader(inputF);
				bfr = new BufferedReader(fr);
				return true;
			} catch (IOException e) {
				fr = null;
				bfr = null;
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean setOutput(String outputFileName) {
		outputF = new File(outputFileName);
		if (outputF.exists()) {
			return false;
		} else {
			try {
				outputF.createNewFile();
			} catch (IOException e) {
				return false;
			}
			
			try {
				if (bfw!=null) {
					bfw.close();
				}
				if (fw!=null) {
					fw.close();
				}
				fw = new FileWriter(outputF);
				bfw = new BufferedWriter(fw);
				return true;
			} catch (IOException e) {
				fw = null;
				bfw = null;
				return false;
			}
		}
	}

	public BufferedReader getBfr() {
		return bfr;
	}

	public BufferedWriter getBfw() {
		return bfw;
	}
	
	public boolean close() {
		try {
			if (bfw!=null) {
				bfw.close();
			}
			if (fw!=null) {
				fw.close();
			}
			if (bfr!=null) {
				bfr.close();
			}
			if (fr!=null) {
				fr.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
