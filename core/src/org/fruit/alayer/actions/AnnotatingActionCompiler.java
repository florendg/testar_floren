/***************************************************************************************************
*
* Copyright (c) 2013 - 2020 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2020 Open Universiteit - www.ou.nl
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/

package org.fruit.alayer.actions;

import java.util.List;

import org.fruit.Util;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.OrthogonalPosition;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Position;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Shape;
import org.fruit.alayer.StrokeCaps;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.visualizers.EllipseVisualizer;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.alayer.visualizers.TextVisualizer;
import org.fruit.alayer.visualizers.TrajectoryVisualizer;
import org.apache.commons.text.StringEscapeUtils;

public class AnnotatingActionCompiler extends StdActionCompiler {
	
	private static final Pen MovePen = Pen.newPen().setColor(Color.Aqua)
			.setFillPattern(FillPattern.None).setStrokeWidth(3).build();
	private static final Pen LClickPen = Pen.newPen().setColor(Color.Green)
			.setFillPattern(FillPattern.Solid).setStrokeWidth(3).build();
	private static final Pen RClickPen = Pen.newPen().setColor(Color.Yellow)
			.setFillPattern(FillPattern.None).setStrokeWidth(5).build();
	private static final Pen DoubleLClickPen = Pen.newPen().setColor(Color.Green) //Color.Red
			.setFillPattern(FillPattern.None).setStrokeWidth(5).build();
	private static final Pen DragDropPen = Pen.newPen().setColor(Color.CornflowerBlue)
			.setFillPattern(FillPattern.None).setStrokeWidth(2).setStrokeCaps(StrokeCaps._Arrow).build();

	private static final Pen DropdownPen = Pen.newPen().setColor(Color.Gold).setFillPattern(FillPattern.None).setStrokeWidth(3).setFontSize(26).build();

	private static final Pen TypePen = Pen.newPen().setColor(Color.Blue)
			//.setFillPattern(FillPattern.None).setStrokeWidth(3).setFontSize(26).build();
			.setFillPattern(FillPattern.None).setStrokeWidth(3).build(); // use default font size
	
	public AnnotatingActionCompiler(){ super(); }
	
	public AnnotatingActionCompiler(Abstractor abstractor){
		super(abstractor);
	}

	
	@Override
	public Action mouseMove(Widget w){
		Action ret = super.mouseMove(w);
		ret.set(Tags.Desc, "Mouse move");
		ret.set(Tags.Role, ActionRoles.MouseMove);
		ret.set(Tags.OriginWidget, w);
		return ret;
	}
	
	@Override
	public Action mouseMove(Widget w, Position position){
		Action ret = super.mouseMove(w,position);
		Shape s = w.get(Tags.Shape);
		Shape moveShape = Rect.from(s.x(), s.y() + s.height(), s.width(), 1); // Rect.from(s.x() + s.width()/4, s.y() + s.height(), s.width()/2, 1);
		ret.set(Tags.Visualizer, new ShapeVisualizer(MovePen, moveShape, "", 0.5, 0.5));
		ret.set(Tags.OriginWidget, w);
		return ret;
	}

	@Override
	public Action leftClick(){
		Action ret = super.leftClick();
		ret.set(Tags.Desc, "Left Click");
		ret.set(Tags.Role, ActionRoles.LeftClick);
		return ret;
	}

	@Override
	public Action rightClick(){
		Action ret = super.rightClick();
		ret.set(Tags.Desc, "Right Click");
		ret.set(Tags.Role, ActionRoles.RightClick);
		return ret;
	}

	@Override
	public Action leftDoubleClick(){
		Action ret = super.leftDoubleClick();
		ret.set(Tags.Desc, "Left Double Click");
		ret.set(Tags.Role, ActionRoles.LDoubleClick);
		return ret;
	}

	@Override
	public Action leftClickAt(Widget widget, double relX, double relY){
		//Action ret = leftClickAt(new WidgetPosition(wf, Tags.Shape, relX, relY, true));
		Action ret = super.leftClickAt(widget, relX, relY); // by urueda
		ret.set(Tags.Desc, "Left Click at '" + widget.get(Tags.Desc, "<no description>") + "'");
		//ret.set(Tags.Targets, Util.newArrayList(wf));
        ret.set(Tags.OriginWidget, widget);
		return ret;
	}

	@Override
	public Action leftClickAt(Position position){
		Action ret = super.leftClickAt(position);
		ret.set(Tags.Desc, "Left Click at '" + position.toString() + "'");
		ret.set(Tags.Visualizer, new EllipseVisualizer(position, LClickPen, 10, 10));
		ret.set(Tags.Role, ActionRoles.LeftClickAt);
		return ret;
	}

	@Override
	public Action rightClickAt(Position position){
		Action ret = super.rightClickAt(position);
		ret.set(Tags.Desc, "Right Click at '" + position.toString() + "'");
		ret.set(Tags.Visualizer, new EllipseVisualizer(position, RClickPen, 20, 20));
		ret.set(Tags.Role, ActionRoles.RightClickAt);
		return ret;
	}

	@Override
	public Action leftDoubleClickAt(Position position){
		Action ret = super.leftDoubleClickAt(position);
		ret.set(Tags.Desc, "Double-Click at '" + position.toString() + "'");
		ret.set(Tags.Visualizer, new EllipseVisualizer(position, DoubleLClickPen, 30, 30));
		ret.set(Tags.Role, ActionRoles.LDoubleClickAt);
		return ret;
	}

	private final int DISPLAY_TEXT_MAX_LENGTH = 16;
	
	@Override
	public Action clickTypeInto(final Widget widget, double relX, double relY, final String text, boolean replaceText){
		//Action ret = clickTypeInto(new WidgetPosition(abstractor.apply(widget), Tags.Shape, relX, relY, true), text);
		Action ret = super.clickTypeInto(widget, relX, relY, text, replaceText);
		//ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, 5, "...") + "' into '" + widget.get(Tags.Desc, "<no description>" + "'"));
		ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "...") + "' into '" + widget.get(Tags.Desc, "<no description>" + "'")); // by urueda
		ret.set(Tags.OriginWidget, widget);
        return ret;
	}

	@Override
	public Action clickAndReplaceText(final Position position, final String text){
		Action ret = super.clickAndReplaceText(position, text);
		//ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate(text, 5, "..."), TypePen));
		ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "..."), TypePen));
		//ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, 5, "...") + "' into '" + position.toString() + "'");
		ret.set(Tags.Desc, "Replace '" + Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "...") + "' into '" + position.toString() + "'");
		ret.set(Tags.Role, ActionRoles.ClickTypeInto);		
		return ret;
	}

	@Override
	public Action clickAndAppendText(final Position position, final String text){
		Action ret = super.clickAndAppendText(position, text);
		//ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate(text, 5, "..."), TypePen));
		ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "..."), TypePen));
		//ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, 5, "...") + "' into '" + position.toString() + "'");
		ret.set(Tags.Desc, "Append '" + Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "...") + "' into '" + position.toString() + "'");
		ret.set(Tags.Role, ActionRoles.ClickTypeInto);
		return ret;
	}
	
	@Override
	public Action pasteAndReplaceText(final Position position, final String text){
		Action ret = super.pasteAndReplaceText(position, text);
		ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate("pasted text", DISPLAY_TEXT_MAX_LENGTH, "..."), TypePen));
		ret.set(Tags.Role, ActionRoles.PasteTextInto);
		ret.set(Tags.Desc, "Paste Text: " + StringEscapeUtils.escapeHtml4(text));
		return ret;
	}

	@Override
	public Action pasteAndAppendText(final Position position, final String text){
		Action ret = super.pasteAndAppendText(position, text);
		ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate("pasted text", DISPLAY_TEXT_MAX_LENGTH, "..."), TypePen));
		ret.set(Tags.Role, ActionRoles.PasteTextInto);
		ret.set(Tags.Desc, "Append Paste Text: " + StringEscapeUtils.escapeHtml4(text));
		return ret;
	}

	@Override
	public Action dropDownAt(Position position){
	  Action ret = super.dropDownAt(position);
	  ret.set(Tags.Desc, "Dropdown at '" + position.toString() + "'");
	  ret.set(Tags.Visualizer, new EllipseVisualizer(position, DropdownPen, 30, 30));
	  ret.set(Tags.Role, ActionRoles.DropDown);
	  return ret;
	}	
	
	@Override
	public Action dragFromTo(Position from, Position to){
		Action ret = super.dragFromTo(from, to);
		ret.set(Tags.Visualizer, new TrajectoryVisualizer(DragDropPen, from, new OrthogonalPosition(from, to, 0.2, 0), to));
		ret.set(Tags.Desc, "Drag " + from.toString() + " To " + to.toString());
		ret.set(Tags.Role, ActionRoles.LeftDrag);		
		return ret;
	}
	
	@Override
	public Action hitKey(int key){
		Action ret = super.hitKey(key);
		ret.set(Tags.Desc, "Hit Key " + key);
		ret.set(Tags.Role, ActionRoles.HitKey);		
		return ret;
	}
	
	@Override
	public Action hitShortcutKey(List<Integer> keys){
		Action ret = super.hitShortcutKey(keys);
		String keysString = "";
		for (int i = 0; i < keys.size(); i++)
			keysString += i != 0 ? "+" : "" + keys.get(i);
		ret.set(Tags.Desc, "Hit Shortcut Key " + keysString);
		ret.set(Tags.Role, ActionRoles.HitShortcutKey);		
		return ret;
	}	
	
	@Override
	public Action killProcessByPID(long pid, double timeToWaitBeforeKilling){
		Action ret = super.killProcessByPID(pid, timeToWaitBeforeKilling);
		ret.set(Tags.Desc, "Kill Process with pid: " + pid + ".");
		return ret;
	}
	
	@Override
	public Action killProcessByName(String name, double timeToWaitBeforeKilling){
		Action ret = super.killProcessByName(name, timeToWaitBeforeKilling);
		ret.set(Tags.Desc, "Kill Process with name '" + name + "'");
		return ret;
	}
	
	@Override
	public Action activateSystem(){	
		Action ret = super.activateSystem();
		ret.set(Tags.Desc, "Bring the system to the foreground.");
		return ret;
	}
}
