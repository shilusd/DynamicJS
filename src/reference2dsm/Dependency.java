package reference2dsm;

public class Dependency {
	private String srcFileName;
	private String srcFuncName;
	private String dstFileName;
	private String dstFuncName;
	private String dependencyType;
	
	public Dependency() {
	}

	public Dependency(String srcFileName, String srcFuncName, String dstFileName, String dstFuncName,
			String dependencyType) {
		super();
		this.srcFileName = srcFileName;
		this.srcFuncName = srcFuncName;
		this.dstFileName = dstFileName;
		this.dstFuncName = dstFuncName;
		this.dependencyType = dependencyType;
	}

	public String getSrcFileName() {
		return srcFileName;
	}

	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public String getSrcFuncName() {
		return srcFuncName;
	}

	public void setSrcFuncName(String srcFuncName) {
		this.srcFuncName = srcFuncName;
	}

	public String getDstFileName() {
		return dstFileName;
	}

	public void setDstFileName(String dstFileName) {
		this.dstFileName = dstFileName;
	}

	public String getDstFuncName() {
		return dstFuncName;
	}

	public void setDstFuncName(String dstFuncName) {
		this.dstFuncName = dstFuncName;
	}

	public String getDependencyType() {
		return dependencyType;
	}

	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	}

	@Override
	public String toString() {
		return "Dependency [srcFileName=" + srcFileName + ", srcFuncName=" + srcFuncName + ", dstFileName="
				+ dstFileName + ", dstFuncName=" + dstFuncName + ", dependencyType=" + dependencyType + "]";
	}
}
