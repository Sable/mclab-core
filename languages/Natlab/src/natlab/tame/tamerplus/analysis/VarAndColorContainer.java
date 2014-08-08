package natlab.tame.tamerplus.analysis;

import java.util.HashSet;

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

	public static void main(String args[]) {
		VarAndColorContainer var1 = new VarAndColorContainer("mc_t1", 1);
		HashSet<VarAndColorContainer> set = new HashSet<VarAndColorContainer>();
		set.add(var1);
		if (set.contains(new VarAndColorContainer("mc_t1", 1))) {
			System.out.println("Success");
		} else {
			System.out.println("Nope");
		}
	}

}
