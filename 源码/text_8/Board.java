package text_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import shapes.Shape;

public class Board {
	private List<Shape> shapes;
	private GC gcMain;
	private Display display;
	
	public Board(GC gc, Display di) {
		shapes = new ArrayList<Shape>();
		gcMain = gc;
		display = di;
	}

	public void InsertShape(Shape shape) {
		shapes.add(shape);
	}

	public void Refresh() {
		for (Shape shape : shapes) {
			shape.Draw();
		}
	}

	public void save(String filename) throws IOException {
		PrintStream out = new PrintStream(new File(filename));
		// 存入图形数量
		out.println(shapes.size());
		System.out.println(shapes.size());
		for (Shape shape : shapes) {
			writeShape(out, shape);
		}
		out.close();
	}

	public void open(String filename, Point pt) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		// 清空（覆盖）之前绘制的图形
		gcMain.fillRectangle(0, 0, pt.x, pt.y);
		shapes.clear();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line = reader.readLine();
		int shapeCount = Integer.parseInt(line);

		for (int i = 0; i < shapeCount; i++) {
			Shape shape = readShape(reader);
			InsertShape(shape);
		}
		reader.close();
		
		Refresh();
	}

	private void writeShape(PrintStream out, Shape shape) throws IOException {
		out.println(shape.getClass().getName());
		out.println(shape.getTop());
		out.println(shape.getLeft());
		out.println(shape.getWidth());
		out.println(shape.getHeight());
		out.println(shape.getColor());
		out.println(shape.getLine());
	}

	private Shape readShape(BufferedReader in) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String className = in.readLine();
		String top = in.readLine();
		String left = in.readLine();
		String width = in.readLine();
		String height = in.readLine();
		String color = in.readLine();
		String line = in.readLine();
		
		int t = Integer.parseInt(top);
		int l = Integer.parseInt(left);
		int w = Integer.parseInt(width);
		int h = Integer.parseInt(height);
		int c = Integer.parseInt(color);
		int li = Integer.parseInt(line);

		Shape shape = null;
			Class<?> shapeClass = Class.forName(className);
			Object oShape = shapeClass.newInstance();
			shape = (Shape) oShape;
			shape.setTop(t);
			shape.setLeft(l);
			shape.setWidth(w);
			shape.setHeight(h);
			shape.setColor(c);
			shape.setLine(li);
			shape.setGcMain(gcMain);
			shape.setDisplay(display);
		return shape;
	}
}
