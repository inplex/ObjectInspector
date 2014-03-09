package me.inplex.objectinspector;

public class ObjectChange {
	
	private String name;
	private Object from;
	private Object to;
	
	public ObjectChange(String name, Object from, Object to) {
		this.name = name;
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String toString() {
		return "ObjectChange[name=" + name + " from=" + from + " to=" + to + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getFrom() {
		return from;
	}

	public void setFrom(Object from) {
		this.from = from;
	}

	public Object getTo() {
		return to;
	}

	public void setTo(Object to) {
		this.to = to;
	}
	
}