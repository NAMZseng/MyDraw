package shapes;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class Circle implements Shape {
	private int top;
	private int left;
	private int width;
	private int height;
	private int color;
	private int line;
	private GC gcMain;
	private Display display;

	private static final String toolText = "т╡пн";

	public static String getToolText() {
		return toolText;
	}

	public Circle() {

	}

	public Circle(int top, int left, int width, int height, GC gc) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
		this.gcMain = gc;
	}

	@Override
	public void Draw() {
		gcMain.setLineStyle(line);
		gcMain.setForeground(display.getSystemColor(color));
		gcMain.drawOval(top, left, width, height);
	}

	@Override
	public int getTop() {
		return top;
	}

	@Override
	public void setTop(int top) {
		this.top = top;
	}

	@Override
	public int getLeft() {
		return left;
	}

	@Override
	public void setLeft(int left) {
		this.left = left;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public void setGcMain(GC gcMain) {
		this.gcMain = gcMain;
	}
	
    @Override
    public void setDisplay(Display display) {
    	this.display = display;
    }

}
