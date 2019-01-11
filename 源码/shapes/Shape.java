package shapes;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public interface Shape {
	public void Draw();

	public int getTop();

	public void setTop(int top);

	public int getLeft();

	public void setLeft(int left);

	public int getWidth();

	public void setWidth(int width);

	public int getHeight();

	public void setHeight(int height);

	public int getColor();

	public void setColor(int color);

	public int getLine();

	public void setLine(int line);

	public void setGcMain(GC gcMain);
	
	public void setDisplay(Display display);
	
	
}
