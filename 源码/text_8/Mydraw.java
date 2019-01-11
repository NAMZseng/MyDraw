package text_8;

import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import shapes.Shape;

/*
 * 实验内容：
 * 1. 实现保存读取（二进制或文本格式均可） （V12.0）
 * 2. 实现绘图线型和画笔颜色
 * 3. 完成最终的实验绘图程序
 */
public class Mydraw extends Shell {
	private static Display display;
	private static Shell shell;
	private static Menu menu;
	private static MenuItem shapeSubmenu;
	private static Menu menu_1; // 创建图形菜单下拉栏，自动添加图形类按钮
	private static Board board;
	private static GC gcMain;

	private static String shapeType = "shapes.Rect";
	private static int startX;
	private static int startY;
	private static boolean leftButtonDown = false;
	private static int lastWidth;
	private static int lastHeight;
	private static int line = SWT.LINE_SOLID;
	private static int color = SWT.COLOR_BLACK;


	private static void draw(Shell shell, Display display) {
		gcMain = new GC(shell);
		board = new Board(gcMain, display);

		List<?> listClass = null;
		String pkg = "shapes";

		// 使用ClassUtil工具类，获取pkg目录下的类
		listClass = ClassUtil.getClassList(pkg, true, null);
		ArrayList<String> shapeTypes = new ArrayList<String>();

		// 获取完整类名
		for (Object object : listClass) {
			String name = ((Class<?>) object).getName();
			if (!name.equals("shapes.Shape")) {
				shapeTypes.add(name);
			}
		}

		// 批量添加各个图形类对应的按钮，及对应的监听器
		for (String strClass : shapeTypes) {
			MenuItem mShapeItem = new MenuItem(menu_1, SWT.NONE);
			try {
				Class<?> shapeClass = Class.forName(strClass);
				Method method = shapeClass.getMethod("getToolText");
				mShapeItem.setText(method.invoke(null, null).toString());
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return;
			}
			mShapeItem.addSelectionListener(new SelectionAdapter() {
				@Override
				// 当Rect按钮组件widget被选择时
				public void widgetSelected(SelectionEvent e) {
					shapeType = strClass;
				}
			});
		}

		// 设置Paint监听器，当窗口被遮挡/或缩放后，进行重绘
		shell.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				board.Refresh();
			}
		});

		// 设置鼠标移动的监听机制，实现移动鼠标绘图时的视觉辅助
		shell.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				if (leftButtonDown) {
					gcMain.setLineStyle(SWT.LINE_DOT);

					// 用背景色重绘，覆盖上一次的虚线
					gcMain.setForeground(shell.getBackground());
					gcMain.drawRectangle(startX, startY, lastWidth, lastHeight);

					// 绘制本次移动的虚线，并设为蓝色
					gcMain.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
					gcMain.drawRectangle(startX, startY, arg0.x - startX, arg0.y - startY);

					lastWidth = arg0.x - startX;
					lastHeight = arg0.y - startY;
				}
			}
		});

		// 设置与鼠标点击相关的监听机制，实现画图
		shell.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					leftButtonDown = false;
					int width = e.x - startX;
					int height = e.y - startY;

					// 以背景色重绘最后一次的虚线矩形，已达到擦除的目的
					gcMain.setLineStyle(SWT.LINE_DOT);
					gcMain.setForeground(shell.getBackground());
					gcMain.drawRectangle(startX, startY, width, height);

					// 绘制最终的shapType图形
					Shape shape = null;
					try {
						Class<?> shapeClass = Class.forName(shapeType);
						Object oShape = shapeClass.newInstance();
						shape = (Shape) oShape;
						shape.setTop(startX);
						shape.setLeft(startY);
						shape.setWidth(e.x - startX);
						shape.setHeight(e.y - startY);
						shape.setColor(color);
						shape.setLine(line);
						shape.setGcMain(gcMain);
						shape.setDisplay(display);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					if (shape != null) {
						board.InsertShape(shape);
						// 重绘所有图形，解决在擦除虚线框时对其他已绘图形的覆盖
						board.Refresh();
					}
					// 设置鼠标在抬起后恢复为默认值
					shell.setCursor(new Cursor(null, 0));
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					// 设置鼠标在按下时显示为十字
					shell.setCursor(new Cursor(null, SWT.CURSOR_CROSS));
					leftButtonDown = true;
					startX = e.x;
					startY = e.y;
				}
			}
		});
	}

	public static void main(String args[]) {
		try {
			display = Display.getDefault();
			shell = new Mydraw(display);
			shell.open();
			shell.layout();

			draw(shell, display);

			// 反复的读取和分派（dispatch）事件，并在没有事件的时候把控制权还给CPU
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Mydraw(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new GridLayout(1, false));

		menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);

		MenuItem fileSubmenu = new MenuItem(menu, SWT.CASCADE);
		fileSubmenu.setText("文件");

		Menu menu_4 = new Menu(fileSubmenu);
		fileSubmenu.setMenu(menu_4);

		MenuItem mFileItemOpen = new MenuItem(menu_4, SWT.NONE);
		mFileItemOpen.setText("打开文件");
		mFileItemOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Display display_open = Display.getDefault();
				Mydraw shell_open = new Mydraw(display_open);

				FileDialog fd = new FileDialog(shell_open, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.txt", "*.*" });
				fd.setFilterNames(new String[] { "Text Files(*.txt)", "All Files(*.*)" });
				String openFile = fd.open();
				if (openFile != null) {
					try {
						Point pt = shell.getSize();
						board.open(openFile, pt);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		MenuItem mFileItemSave = new MenuItem(menu_4, SWT.NONE);
		mFileItemSave.setText("保存文件");
		mFileItemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Display display_save = Display.getDefault();
				Mydraw shell_save = new Mydraw(display_save);

				FileDialog fd = new FileDialog(shell_save, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.txt", "*.*" });
				fd.setFilterNames(new String[] { "Text Files(*.txt)", "All Files(*.*)" });
				String saveFile = fd.open();
				if (saveFile != null) {
					try {
						board.save(saveFile);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		shapeSubmenu = new MenuItem(menu, SWT.CASCADE);
		shapeSubmenu.setText("图形");

		menu_1 = new Menu(shapeSubmenu);
		shapeSubmenu.setMenu(menu_1);

		MenuItem lineSubmenu = new MenuItem(menu, SWT.CASCADE);
		lineSubmenu.setText("线型");

		Menu menu_2 = new Menu(lineSubmenu);
		lineSubmenu.setMenu(menu_2);

		MenuItem mlineItemSolid = new MenuItem(menu_2, SWT.NONE);
		mlineItemSolid.setText("直线");
		mlineItemSolid.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				line = SWT.LINE_SOLID;
			}
		});

		MenuItem mlineItemDot = new MenuItem(menu_2, SWT.NONE);
		mlineItemDot.setText("虚线");
		mlineItemDot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				line = SWT.LINE_DOT;
			}

		});

		MenuItem mlineItemDash = new MenuItem(menu_2, SWT.NONE);
		mlineItemDash.setText("划线");
		mlineItemDash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				line = SWT.LINE_DASH;
			}

		});

		MenuItem colorSubmenu = new MenuItem(menu, SWT.CASCADE);
		colorSubmenu.setText("颜色");

		Menu menu_3 = new Menu(colorSubmenu);
		colorSubmenu.setMenu(menu_3);

		MenuItem mColorItemBlack = new MenuItem(menu_3, SWT.NONE);
		mColorItemBlack.setText("黑色");
		mColorItemBlack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				color = SWT.COLOR_BLACK;
			}
		});

		MenuItem mColorItemRed = new MenuItem(menu_3, SWT.NONE);
		mColorItemRed.setText("红色");
		mColorItemRed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				color = SWT.COLOR_RED;
			}
		});

		MenuItem mColorItemBule = new MenuItem(menu_3, SWT.NONE);
		mColorItemBule.setText("蓝色");
		mColorItemBule.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				color = SWT.COLOR_BLUE;
			}
		});

		createContents();
	}

	protected void createContents() {
		setText("Mydraw");
		setSize(1000, 618);
		
		// 获取屏幕高度和宽度
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		// 获取对象窗口高度和宽度
		int shellH = getBounds().height;
		int shellW = getBounds().width;
		//定位对象窗口坐标
        setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
