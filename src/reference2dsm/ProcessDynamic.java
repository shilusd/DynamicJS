package reference2dsm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProcessDynamic {
	private static int[][] readDsm(IOUtil io, HashMap<String, Integer> typeno, HashMap<String, Integer> fileno, String proj ) throws IOException {
		BufferedReader bfr = io.getBfr();
		if (bfr!=null) {
			String s = bfr.readLine();
			String[] types = s.substring(1, s.length()-1).split(",");
			for (int i=0;i<types.length;i++) {
				typeno.put(types[i], i);
			}
			
			int n = Integer.parseInt(bfr.readLine());
			int[][] map = new int[n][n];
			for (int i=0;i<n;i++) {
				s = bfr.readLine();
				String[] rel = s.split(" ");
				for (int j=0;j<n;j++) {
					map[i][j] = Integer.parseInt(rel[j],2);
				}
			}
			
			for (int i=0;i<n;i++) {
				s = bfr.readLine();
				s = s.substring(s.indexOf(proj));
				fileno.put(s, i);
			}
			return map;
		} else {
			return null;
		}
	}
	
	private static int[][] readAndMerge(IOUtil io, HashMap<String,Integer> typeNo, HashMap<String,Integer> fileNo, int[][] map, String newType) throws IOException {
		BufferedReader bfr = io.getBfr();
		if (bfr!=null) {
			typeNo.put(newType, typeNo.size());
			for (int i=0;i<map.length;i++) {
				for (int j=0;j<map.length;j++) {
					map[i][j] = map[i][j]*2;
				}
			}
			
			String s = bfr.readLine();
			while(s!=null) {
				int src = fileNo.get(s.split(",")[0]);
				int dst = fileNo.get(s.split(",")[1]);
				if (map[src][dst]%2==0) {
					map[src][dst] = map[src][dst]+1;
				}
				s = bfr.readLine();
			}
		}
		
		return map;
	}
	
	private static int[][] calcTotal(HashMap<String,Integer> typeNo, int[][] map, int n) {
		typeNo.put("AllNew", typeNo.size());
		for (int i=0;i<map.length;i++) {
			for (int j=0;j<map.length;j++) {
				long mod = map[i][j]%Math.round(Math.pow(2, n));
				map[i][j] = map[i][j]*2;
				if (mod!=0) {
					map[i][j] = map[i][j]+1;
				}
			}
		}
		return map;		
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
			for (int i=0;i<type.length;i++) {
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
	
	//from understand report
		public static void run(String dsmFile, String[] newFiles, String dstFile) throws IOException {
			IOUtil io = new IOUtil();
			int[][] map;
			HashMap<String,Integer> typeNo = new HashMap<String,Integer>();
			HashMap<String,Integer> fileNo = new HashMap<String,Integer>();
			
			
			io.setInput(dsmFile);
			map = readDsm(io,typeNo,fileNo,dsmFile.substring(0,dsmFile.lastIndexOf(".")-1));
			io.close();
			
			for (int i=2;i<newFiles.length;i++) {
				io = new IOUtil();
				io.setInput(newFiles[i]);
				readAndMerge(io,typeNo,fileNo,map,newFiles[i].substring(0, newFiles[i].lastIndexOf(".")-1));
				io.close();
			}
			map = calcTotal(typeNo,map,newFiles.length-2);
			
			io = new IOUtil();
			io.setOutput(dstFile);
			
			writeDsm(io,typeNo,fileNo,map);
			io.close();
		}
}
