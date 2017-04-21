package reference2dsm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ProcessScript {
	private static boolean init(IOUtil io) {
		BufferedReader bfr = io.getBfr();
		if (bfr!=null) {
			try {
				bfr.readLine();
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}	
	}
	
	private static LinkedList<Dependency> findDependency(IOUtil io, HashMap<String,Integer> typeMask, HashMap<String,Integer> funcNo) {
		if (!init(io)) {
			return null;
		};
		BufferedReader bfr = io.getBfr();
		if (bfr==null) {
			return null;
		}
		LinkedList<Dependency> ret = new LinkedList<Dependency>();
		
		int count=0;
		while (true) {
			String readl="";
			try {
				readl = bfr.readLine();
			} catch (IOException e) {
				break;
			}
			if (readl==null||readl.equals("")) {
				break;
			}
			count++;
			String[] dependencyString = readl.split(",");
			Dependency dependency = new Dependency();
			String srcFuncName = "FUNC:"+dependencyString[0].replace('.', '_');
			String srcFileName = dependencyString[1].replace('.', '_').replace('\\', '.');
			String dependencyType = dependencyString[4];
			String dstFuncName = dependencyString[3].replace('.', '_');
			String dstFileName = dependencyString[5].replace('.', '_').replace('\\', '.');
			
			if (dependencyType.trim().equals("Javascript Call")) {
				dstFuncName = "FUNC:"+dstFuncName.concat("()");
			} else {
				dstFuncName = "VAR:"+dstFuncName;
			}
			
			dependency.setDependencyType(dependencyType);
			dependency.setSrcFileName(srcFileName);
			dependency.setSrcFuncName(srcFuncName);
			dependency.setDstFuncName(dstFuncName);
			dependency.setDstFileName(dstFileName);
			
			ret.add(dependency);
			
			if (!typeMask.containsKey(dependency.getDependencyType())) {
				typeMask.put(dependency.getDependencyType(), typeMask.size());
			}
			if (!funcNo.containsKey(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))) {
				funcNo.put(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()), funcNo.size());
			}
			if (!funcNo.containsKey(dependency.getDstFileName().concat("."+dependency.getDstFuncName()))) {
				funcNo.put(dependency.getDstFileName().concat("."+dependency.getDstFuncName()), funcNo.size());
			}
		}
		System.out.println("Dependency: "+count);
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
					if (dependencyMap[i][j]==0) {
						sb.append(0);
					} else {
						String dsm = Integer.toString(dependencyMap[i][j], 2);
						while (dsm.length()<typeMask.size()) {
							dsm = "0"+dsm;
						}
						sb.append(dsm);
					}
					sb.append(" ");
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
	
	//from understand script
	public static void run(String srcFile, String dstFile) {
		IOUtil io = new IOUtil();
		LinkedList<Dependency> list = new LinkedList<Dependency>();
		HashMap<String,Integer> typeMask = new HashMap<String,Integer>();
		HashMap<String,Integer> funcNo = new HashMap<String,Integer>();
		
		
		io.setInput(srcFile);
		io.setOutput(dstFile);
		list = findDependency(io,typeMask, funcNo);
		int[][] dependencyMap = new int[funcNo.size()][funcNo.size()];
		while(!list.isEmpty()) {
			Dependency dependency = list.removeFirst();
			//System.out.print(Integer.toString(dependencyMap[funcNo.get(dependency.getSrcFullName())][funcNo.get(dependency.getDstFullName())], 2)
			//		+ "," +Integer.toString((int)Math.round(Math.pow(2, typeMask.get(dependency.getDependencyType()))), 2));
			dependencyMap[funcNo.get(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))][funcNo.get(dependency.getDstFileName().concat("."+dependency.getDstFuncName()))]
					= dependencyMap[funcNo.get(dependency.getSrcFileName().concat("."+dependency.getSrcFuncName()))][funcNo.get(dependency.getDstFileName().concat("."+dependency.getDstFuncName()))]|(int)Math.round(Math.pow(2, typeMask.get(dependency.getDependencyType())));
			//System.out.println(","+Integer.toString(dependencyMap[funcNo.get(dependency.getSrcFullName())][funcNo.get(dependency.getDstFullName())], 2));
		}
		writeDsm(io,typeMask,funcNo,dependencyMap);
		io.close();
	}
}
