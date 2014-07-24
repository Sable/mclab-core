package natlab.tame.tamerplus.analysis;

public class VarAndColorContainer {
	private String varName;
	private Integer color;

	VarAndColorContainer(String varName, Integer color) {
		this.color = color;
		this.varName = varName;
	}

	@Override
	public int hashCode() {
		return varName.hashCode() + color.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VarAndColorContainer)) {
			return false;
		}
		VarAndColorContainer obj = (VarAndColorContainer) o;
		return varName.equals(obj.getVarName()) && color.equals(obj.getColor());
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

}
