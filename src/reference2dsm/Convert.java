package reference2dsm;

import java.io.IOException;

public class Convert {
	public static void main(String[] args) throws IOException {
		//ProcessReport.run(args[0], args[1]);
		//ProcessScript.run(args[0], args[1]);
		//ProcessReportWithNum.run(args[0], args[1]);
		ProcessDynamic.run(args[1],args,args[0]);
	}
}
