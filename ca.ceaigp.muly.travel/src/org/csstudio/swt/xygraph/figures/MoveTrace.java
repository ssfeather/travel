package org.csstudio.swt.xygraph.figures;


import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.undo.ZoomType;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory.CURSOR_TYPE;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Cursor;

public class MoveTrace extends Trace
{
	private ZoomType zoomType;
	final private Cursor grabbing;
	private boolean armed;
	private Point start;
	private Point end;
	
	public MoveTrace(String name, Axis xAxis, Axis yAxis, IDataProvider dataProvider)
    {
		super(name, xAxis, yAxis, dataProvider);
	    // TODO Auto-generated constructor stub
		grabbing = XYGraphMediaFactory.getCursor(CURSOR_TYPE.GRABBING);
    }
	
	/**
	 * Listener to mouse events, performs panning and some zooms Is very similar
	 * to the Axis.AxisMouseListener, but unclear how easy/useful it would be to
	 * base them on the same code.
	 */
	class PlotMouseListener extends MouseMotionListener.Stub implements MouseListener
	{
		public void mousePressed(final MouseEvent me)
		{
			// Only react to 'main' mouse button, only react to 'real' zoom
			//启用zoomType为NONE状态，为波形移动
			//if (me.button != 1 || zoomType == ZoomType.NONE) return;
			if (me.button != 1) return;
			armed = true;
			// get start position
			switch (zoomType)
			{
				case RUBBERBAND_ZOOM:
				case HORIZONTAL_ZOOM:
				case VERTICAL_ZOOM:
				case PANNING:
				case NONE:
					//System.out.println("**** MousePressed NONE ****");
					setCursor(grabbing);
					start = me.getLocation();
					end = null;
					break;
				case ZOOM_IN:
				case ZOOM_IN_HORIZONTALLY:
				case ZOOM_IN_VERTICALLY:
				case ZOOM_OUT:
				case ZOOM_OUT_HORIZONTALLY:
				case ZOOM_OUT_VERTICALLY:
				default:
					break;
			}

			// add command for undo operation
			//command = new ZoomCommand(zoomType.getDescription(), xyGraph.getXAxisList(), xyGraph.getYAxisList());
			//me.consume();
		}

		public void mouseDoubleClicked(final MouseEvent me)
		{ 
			//System.out.println("**** MouseDoubleClicked ****");
		}

		@Override
		public void mouseDragged(final MouseEvent me)
		{
			//System.out.println("**** MouseDragged ****");
			if (!armed) return;
			switch (zoomType)
			{
				case RUBBERBAND_ZOOM:
				case HORIZONTAL_ZOOM:
				case VERTICAL_ZOOM:
				case PANNING:
				case NONE:
					//System.out.println("**** MouseDragged NONE****");
					end = me.getLocation();
					break;
				default:
					break;
			}
			MoveTrace.this.repaint();
		}

		@Override
		public void mouseExited(final MouseEvent me)
		{
			// Treat like releasing the button to stop zoomIn/Out timer
			switch (zoomType)
			{
				case ZOOM_IN:
				case ZOOM_IN_HORIZONTALLY:
				case ZOOM_IN_VERTICALLY:
				case ZOOM_OUT:
				case ZOOM_OUT_HORIZONTALLY:
				case ZOOM_OUT_VERTICALLY:
				default:
			}
		}

		public void mouseReleased(final MouseEvent me)
		{
			if (!armed) return;
			armed = false;
			if (zoomType == ZoomType.PANNING) setCursor(zoomType.getCursor());
			if (end == null || start == null) return;

			switch (zoomType)
			{
				case RUBBERBAND_ZOOM:
				case HORIZONTAL_ZOOM:
				case VERTICAL_ZOOM:
				case PANNING:
				case ZOOM_IN:
				case ZOOM_IN_HORIZONTALLY:
				case ZOOM_IN_VERTICALLY:
				case ZOOM_OUT:
				case ZOOM_OUT_HORIZONTALLY:
				case ZOOM_OUT_VERTICALLY:
				case NONE:
					break;
				default:
					break;
			}

			MoveTrace.this.repaint();
		}


	}
}

