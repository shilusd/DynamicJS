package reference2dsm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ProcessReportWithNum {
	private static boolean init(IOUtil io) {
		BufferedReader bfr = io.getBfr();
		if (bfr!=null) {
			try {
				bfr.readLine();
				bfr.readLine();
				bfr.readLine();
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	private static LinkedList<Dependency> nextFunction(IOUtil io) {
		BufferedReader bfr = io.getBfr();
		try {
			String readl;
			readl = bfr.readLine();
			String functionName = "func:"+readl.substring(0, readl.indexOf(' ')).replace('.', '_');
			bfr.readLine();
			
			LinkedList<Dependency> list = new LinkedList<Dependency>();
			String defineFile = "";
			while(true) {
				readl=bfr.readLine();
				if (readl==null||readl.equals("")) {
					break;
				}
				String dependencyType = readl.substring(0,readl.indexOf('[')).trim();
				String dependencyFileName = readl.substring(readl.indexOf('['),readl.indexOf(']'));
				dependencyFileName = dependencyFileName.substring(1, dependencyFileName.lastIndexOf(',')).trim();
				String callFunctionName = "func:"+readl.substring(readl.indexOf(']')+1).trim().replace('.', '_');
				if (dependencyType.equals("Javascript Define")) {
					defineFile = dependencyFileName;
				} else if (!defineFile.equals("")){
					//if (!defineFile.equals(dependencyFileName)){
						Dependency dependency = new Dependency();
						dependency.setDependencyType(dependencyType);
						dependency.setDstFileName(defineFile.replace('.', '_').replace('\\', '.'));
						dependency.setDstFuncName(functionName);
						dependency.setSrcFileName(dependencyFileName.replace('.', '_').replace('\\', '.'));
						dependency.setSrcFuncName(callFunctionName);
						//System.out.println(dependency);
						list.add(dependency);
					//}
				}
			}
			
			return list;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static LinkedList<Dependency> findDependency(IOUtil io, HashMap<String,Integer> typeMask, HashMap<String,Integer> funcNo, HashMap<String,Integer> fileNo) {
		if (!init(io)) {
			return null;
		};
		LinkedList<Dependency> list = null;
		LinkedList<Dependency> ret = new LinkedList<Dependency>();
		while ((list = nextFunction(io))!=null) {
			ret.addAll(list);
			while(!list.isEmpty()) {
				Dependency dependency = list.removeFirst();
				if (!typeMask.containsKey(dependency.getDependencyType())) {
					typeMask.put(dependency.getDependencyType(), typeMask.size());
				}
				if (!funcNo.containsKey(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))) {
					funcNo.put(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()), funcNo.size());
				}
				if (!funcNo.containsKey(dependency.getDstFileName().concat("."+dependency.getDstFuncName()))) {
					funcNo.put(dependency.getDstFileName().concat("."+dependency.getDstFuncName()), funcNo.size());
				}
				if (!fileNo.containsKey(dependency.getSrcFileName())) {
					fileNo.put(dependency.getSrcFileName(), fileNo.size());
				}
				if (!fileNo.containsKey(dependency.getDstFileName())) {
					fileNo.put(dependency.getDstFileName(), fileNo.size());
				}
			}
		}
		return ret;
	}
	
	private static boolean writeDsm(IOUtil io, HashMap<String,Integer> typeMask, HashMap<String,Integer> funcNo, int[][] dependencyMap) {
		BufferedWriter bfw = io.getBfw();
		StringBuffer sb = new StringBuffer();
		if (bfw!=null) {
			//Dependency Type
			String[] type = new String[typeMask.size()];
			Iterator<Map.Entry<String,Integer>> it = typeMask.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String,Integer> entry = it.next();
				type[entry.getValue()] = entry.getKey();
			}
			sb.append("[");
			for (int i=type.length-1;i>=0;i--) {
				sb.append(type[i]);
				sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]\n");
			
			//Number of Function
			int no = funcNo.size();
			sb.append(no);
			sb.append("\n");
			
			//DSM
			for (int i=0;i<no;i++) {
				for (int j=0;j<no;j++) {
					sb.append(dependencyMap[i][j]+" ");
				}
				sb.append("\n");
			}
			
			//Function List
			String[] func = new String[funcNo.size()];
			it = funcNo.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String,Integer> entry = it.next();
				func[entry.getValue()] = entry.getKey();
			}
			for (int i=0;i<func.length;i++) {
				sb.append(func[i]);
				sb.append("\n");
			}
			
			try {
				bfw.write(sb.toString());
			} catch (IOException e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean writeCsv(IOUtil io, HashMap<String,Integer> fileNo, int[][] dependencyMap) {
		BufferedWriter bfw = io.getBfw();
		StringBuffer sb = new StringBuffer();
		if (bfw!=null) {
			Iterator<Map.Entry<String,Integer>> it;
			
			//File List
			String[] file = new String[fileNo.size()];
			it = fileNo.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String,Integer> entry = it.next();
				file[entry.getValue()] = entry.getKey();
			}
			sb.append(",");
			for (int i=0;i<file.length;i++) {
				sb.append(file[i]);
				sb.append(",");
			}
			
			sb.deleteCharAt(sb.length()-1);
			sb.append("\r\n");
			
			
			//DSM
			for (int i=0;i<fileNo.size();i++) {
				sb.append(file[i]+",");
				for (int j=0;j<fileNo.size();j++) {
					sb.append(dependencyMap[i][j]+",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("\r\n");
			}
			
			try {
				bfw.write(sb.toString());
			} catch (IOException e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	//from understand report
		public static void run(String srcFile, String dstFile) {
			IOUtil io = new IOUtil();
			LinkedList<Dependency> list = new LinkedList<Dependency>();
			HashMap<String,Integer> typeMask = new HashMap<String,Integer>();
			HashMap<String,Integer> funcNo = new HashMap<String,Integer>();
			HashMap<String,Integer> fileNo = new HashMap<String,Integer>();
			
			
			io.setInput(srcFile);
			io.setOutput(dstFile);
			list = findDependency(io,typeMask, funcNo, fileNo);
			int[][] dependencyMap = new int[funcNo.size()][funcNo.size()];
			int[] funcIncome = new int[funcNo.size()];
			int[] funcOutcome = new int[funcNo.size()];
			int[] funcInner = new int[funcNo.size()];
			int[] fileIncome = new int[fileNo.size()];
			int[] fileOutcome = new int[fileNo.size()];
			int[][] func2file = new int[funcNo.size()][fileNo.size()];
			
			//[i][j] means number of i call j
			int[][] file2file = new int[fileNo.size()][fileNo.size()];
			int[][] file2func = new int[funcNo.size()][fileNo.size()];
			
			while(!list.isEmpty()) {
				
				Dependency dependency = list.removeFirst();
				if (dependency.getDstFileName().equals(dependency.getSrcFileName())) {
					funcInner[funcNo.get(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))]++;
				} else {
					funcOutcome[funcNo.get(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))]++;
					funcIncome[funcNo.get(dependency.getDstFileName().concat("."+dependency.getDstFuncName()))]++;
					fileOutcome[fileNo.get(dependency.getSrcFileName())]++;
					fileIncome[fileNo.get(dependency.getDstFileName())]++;
				}
				dependencyMap[funcNo.get(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))][funcNo.get(dependency.getDstFileName().concat("."+dependency.getDstFuncName()))]++;
				file2file[fileNo.get(dependency.getSrcFileName())][fileNo.get(dependency.getDstFileName())]++;
				func2file[funcNo.get(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))][fileNo.get(dependency.getDstFileName())]++;
				file2func[funcNo.get(dependency.getDstFileName().concat("."+dependency.getDstFuncName()))][fileNo.get(dependency.getSrcFileName())]++;
			}
			
			//write info
			IOUtil infoIO = new IOUtil();
			infoIO.setOutput("report.txt");
			BufferedWriter bfw = infoIO.getBfw();
			StringBuffer sb = new StringBuffer();
			int i=0;
			
			//file
			sb.append("FILE:\r\n");
			Iterator<Map.Entry<String,Integer>> it = fileNo.entrySet().iterator();
			String[] file = new String[fileNo.size()];
			while(it.hasNext()) {
				i++;
				Map.Entry<String,Integer> entry = it.next();
				file[entry.getValue()] = entry.getKey();
				sb.append(i+". "+entry.getKey().replace('.', '\\').replace('_','.')+"\r\n");
				sb.append("Number of caller File outside: "+fileIncome[entry.getValue()]+"\r\n");
				sb.append("Number of callee File outside: "+fileOutcome[entry.getValue()]+"\r\n");
				sb.append("\r\n");
			}		
			

			//function
			sb.append("FUNCTION:\r\n");
			it = funcNo.entrySet().iterator();
			i=0;
			while (it.hasNext()) {
				i++;
				Map.Entry<String,Integer> entry = it.next();
				sb.append(i+". "+entry.getKey().replace('.', '\\').replace('_','.')+"\r\n");
				sb.append("Number of caller Functions outside: "+funcIncome[entry.getValue()]+"\r\n");
				sb.append("Number of callee Functions outside: "+funcOutcome[entry.getValue()]+"\r\n");
				sb.append("Number of Reference inner: "+funcInner[entry.getValue()]+"\r\n");
				sb.append("List of caller File:\r\n");
				int k=0;
				for (int j=0;j<fileNo.size();j++) {
					if (file2func[entry.getValue()][j]!=0) {
						k++;
						sb.append("\t("+k+") ");
						sb.append(file[j]+" ");
						sb.append(file2func[entry.getValue()][j]+"\r\n");
					}
				}
				sb.append("List of callee File:\r\n");
				k=0;
				for (int j=0;j<fileNo.size();j++) {
					if (func2file[entry.getValue()][j]!=0) {
						k++;
						sb.append("\t("+k+") ");
						sb.append(file[j]+" ");
						sb.append(func2file[entry.getValue()][j]+"\r\n");
					}
				}
				sb.append("\r\n");
			}
			
			sb.append("FILE DEPENDENCY:\r\n");
			for (i=0;i<file.length;i++) {
				for (int j=i+1;j<file.length;j++) {
					sb.append(file[i]+" to "+file[j]+": ["+file2file[i][j]+"/"+file2file[j][i]+"]");
					if (file2file[i][j]+file2file[j][i]<=5) {
						sb.append("Low Relativity.\r\n");
					} else if (file2file[i][j]==0) {
						sb.append("Be Depended.\r\n");
					} else if (file2file[j][i]==0) {
						sb.append("Depend on.\r\n");
					} else if ((double)file2file[i][j]/file2file[j][i]>3) {
						sb.append("Depend on with some exception.\r\n");
						
					} else if ((double)file2file[i][j]/file2file[j][i]<1.0/3.0) {
						sb.append("Be Depended on with some exception.\r\n");
						
					} else {
						sb.append("High Coupling.\r\n");
					}
				}
			}
			
			
			try {
				bfw.write(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			infoIO.close();
			
			writeCsv(io,fileNo,file2file);
			io.close();
		}
}
