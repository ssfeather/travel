package ca.ceaigp.muly.travel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.MoveTrace;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.sc.seis.seisFile.sac.SacTimeSeries;


public class View extends ViewPart
{
	public static final String ID = "ca.ceaigp.muly.travel.view";

	private LightweightSystem lws;

	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements IStructuredContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object parent)
		{
			if (parent instanceof Object[])
			{
				return (Object[]) parent;
			}
			return new Object[0];
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	
	private SacTimeSeries getSacData(String fn)
	{
		SacTimeSeries sac = new SacTimeSeries();
		try
        {
	       sac.read(new File(fn));
        }
        catch (FileNotFoundException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        catch (IOException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return sac;
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		// use LightweightSystem to create the bridge between SWT and draw2D
		lws = new LightweightSystem(new Canvas(parent, SWT.NONE));

		// create a new XY Graph.
		//XYGraph xyGraph = new XYGraph();
		XYGraph swtFigure = new XYGraph();
		
		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(swtFigure);

		swtFigure.setTitle("Seismic Wave");
		// set it as the content of LightwightSystem
		lws.setContents(toolbarArmedXYGraph);
		
		swtFigure.setFont(XYGraphMediaFactory.getInstance().getFont(XYGraphMediaFactory.FONT_TAHOMA));
		swtFigure.primaryXAxis.setTitle("Time");
		swtFigure.primaryYAxis.setTitle("Amplitude");
		swtFigure.primaryXAxis.setRange(new Range(0,200));
		swtFigure.primaryXAxis.setDateEnabled(true);
		swtFigure.primaryYAxis.setAutoScale(true);
		swtFigure.primaryXAxis.setAutoScale(true);
		swtFigure.primaryXAxis.setShowMajorGrid(true);
		swtFigure.primaryYAxis.setShowMajorGrid(true);
		swtFigure.primaryXAxis.setAutoScaleThreshold(0);
		swtFigure.getPlotArea().setShowBorder(true);
		//设置轴不可见
		//swtFigure.primaryXAxis.setVisible(false);
		//swtFigure.primaryYAxis.setVisible(false);
	
		//--------------------------------------------------------------------------------------------------------
		
		SacTimeSeries sac1 = getSacData("/Users/macuser/SeisData/test2.sac");
		CircularBufferDataProvider traceDataProvider1 = new CircularBufferDataProvider(true);
		float[] sacx1 = sac1.getX();
		float[] sacy1 = sac1.getY();
		traceDataProvider1.setBufferSize(sacy1.length);
		traceDataProvider1.setCurrentXDataArray(sacx1);
		traceDataProvider1.setCurrentYDataArray(sacy1);
	
		Trace trace1 = new Trace("Wave1",swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider1);
		swtFigure.addTrace(trace1);
		
		//-------------------------------------------------------------------------------------------------------
		final Axis x2Axis = new Axis("X2", false);
		final Axis y2Axis = new Axis("Y2", true);
		
		x2Axis.setTickLableSide(LabelSide.Secondary);
		y2Axis.setTickLableSide(LabelSide.Secondary);

		x2Axis.setTitle("Time");
		y2Axis.setTitle("Amplitude");
		x2Axis.setRange(new Range(0,200));
		x2Axis.setDateEnabled(true);
		
		y2Axis.setAutoScale(true);
		x2Axis.setAutoScale(true);
		//x2Axis.setShowMajorGrid(true);
		//y2Axis.setShowMajorGrid(true);
		x2Axis.setAutoScaleThreshold(0);
		swtFigure.addAxis(x2Axis);
		swtFigure.addAxis(y2Axis);
		
		//-----------------------------------------------------------------------------------------------------
		
		SacTimeSeries sac2 = getSacData("/Users/macuser/SeisData/test1.sac");
		/*
		Axis x2Axis = swtFigure.primaryXAxis;
		Axis y2Axis = swtFigure.primaryYAxis;
		x2Axis.setVisible(false);
		y2Axis.setVisible(false);
		*/
		
		CircularBufferDataProvider traceDataProvider2 = new CircularBufferDataProvider(true);
		float[] sacx2 = sac2.getX();
		float[] sacy2 = sac2.getY();
		traceDataProvider2.setBufferSize(sacy2.length);
		traceDataProvider2.setCurrentXDataArray(sacx2);
		traceDataProvider2.setCurrentYDataArray(sacy2);
		

		//MoveTrace trace2 = new MoveTrace("Wave2",swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider2);
		MoveTrace trace2 = new MoveTrace("Wave2",x2Axis, y2Axis, traceDataProvider2);
		//Trace trace2 = new Trace("Wave2",swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider2, true);
		//Trace trace2 = new Trace("Wave2",swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider2);
		swtFigure.addTrace(trace2);
		trace2.setXYGraph(swtFigure);
		
		/*
		//Test log4j     
        Properties prop = new Properties();
        try
        {
	        prop.load(Activator.class.getResourceAsStream("log4j.properties"));
        }
        catch (IOException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        PropertyConfigurator.configure(prop);
		Logger logger = Logger.getLogger(View.class);
		logger.log(Level.WARN, "This is a test...");
		*/
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		
	}
}