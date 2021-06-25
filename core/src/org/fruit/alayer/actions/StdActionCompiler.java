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

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Position;
import org.fruit.alayer.StdAbstractor;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.WidgetPosition;
import org.fruit.alayer.actions.CompoundAction.Builder;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.MouseButtons;

public class StdActionCompiler {
	Abstractor abstractor;
	private final Action LMouseDown = new MouseDown(MouseButtons.BUTTON1);
	private final Action RMouseDown = new MouseDown(MouseButtons.BUTTON3);
	private final Action LMouseUp = new MouseUp(MouseButtons.BUTTON1);
	private final Action RMouseUp = new MouseUp(MouseButtons.BUTTON3);
	private final Action NOP = new NOP();

	public StdActionCompiler(){	this(new StdAbstractor()); }

	public StdActionCompiler(Abstractor abstractor){
		this.abstractor = abstractor;
	}


	public Action mouseMove(Widget w){
		Finder wf = abstractor.apply(w);
		Position position = new WidgetPosition(wf, Tags.Shape, 0.5, 0.5, true);
		position.obscuredByChildFeature(false); // even if any other widget is at foreground
		Action ret = mouseMove(w, position);
		ret.set(Tags.OriginWidget, w);
		return ret;
	}
	
	public Action mouseMove(Widget w, Position position){
		return new CompoundAction.Builder().add(new MouseMove(position), 0).add(NOP, 1).build();		
	}
	

	public Action leftClick(){
		return new CompoundAction.Builder().add(LMouseDown, 0)
				.add(LMouseUp, 0).add(NOP, 1).build();
	}

	public Action rightClick(){
		return new CompoundAction.Builder().add(RMouseDown, 0).
				add(RMouseUp, 0).add(NOP, 1).build();
	}

	public Action leftDoubleClick(){
		Action lc = leftClick();
		//return new CompoundAction.Builder().add(lc, 0).
		return new CompoundAction.Builder().add(lc, 0).add(NOP, 0.1).	
				add(lc, 0).add(NOP, 1).build();
	}

	public Action leftClickAt(Position position){
		Assert.notNull(position);
		return new CompoundAction.Builder().add(new MouseMove(position), 1)
				.add(LMouseDown, 0).add(LMouseUp, 0).build();
	}

	public Action leftClickAt(double absX, double absY){
		return leftClickAt(new AbsolutePosition(absX, absY));
	}

	public Action leftClickAt(Widget w){
		return leftClickAt(w, 0.5, 0.5);
	}

	public Action leftClickAt(Widget w, double relX, double relY){
		Finder wf = abstractor.apply(w);
		Action ret = leftClickAt(new WidgetPosition(wf, Tags.Shape, relX, relY, true));
		ret.set(Tags.Targets, Util.newArrayList(wf));
		ret.set(Tags.TargetID, w.get(Tags.ConcreteID));
		ret.set(Tags.OriginWidget, w);
		return ret;
	}

	public Action rightClickAt(Position position){
		Assert.notNull(position);
		return new CompoundAction.Builder().add(new MouseMove(position), 1)
				.add(RMouseDown, 0).add(RMouseUp, 0).build();
	}

	public Action rightClickAt(double absX, double absY){
		return rightClickAt(new AbsolutePosition(absX, absY));
	}

	public Action rightClickAt(Widget w){
		return rightClickAt(w, 0.5, 0.5);
	}

	public Action rightClickAt(Widget w, double relX, double relY){
		Finder wf = abstractor.apply(w);
		Action ret = rightClickAt(new WidgetPosition(wf, Tags.Shape, relX, relY, true));
		ret.set(Tags.Desc, "Right Click at '" + w.get(Tags.Desc, "<no description>") + "'");
		ret.set(Tags.Targets, Util.newArrayList(wf));
		ret.set(Tags.TargetID, w.get(Tags.ConcreteID));
		ret.set(Tags.OriginWidget, w);
		return ret;
	}


	public Action leftTripleClickAt(Position position){
		Assert.notNull(position);
		return new CompoundAction.Builder().add(new MouseMove(position), 1)
				.add(LMouseDown, 0).add(LMouseUp, 0).add(LMouseDown, 0).add(LMouseUp, 0).add(LMouseDown, 0).add(LMouseUp, 0).build();
	}
	
	public Action leftTripleClickAt(double absX, double absY){
		return leftTripleClickAt(new AbsolutePosition(absX, absY));
	}
	
	public Action leftTripleClickAt(Widget w){
		return leftTripleClickAt(w, 0.5, 0.5);
	}
	
	public Action leftTripleClickAt(Widget w, double relX, double relY){
		Finder wf = abstractor.apply(w);
		Action ret = leftTripleClickAt(new WidgetPosition(wf, Tags.Shape, relX, relY, true));
		ret.set(Tags.Targets,  Util.newArrayList(wf));
		ret.set(Tags.TargetID, w.get(Tags.ConcreteID));
		return ret;
	}
	

	public Action leftDoubleClickAt(Position position){
		Assert.notNull(position);
		return new CompoundAction.Builder().add(new MouseMove(position), 1)
				.add(LMouseDown, 0).add(LMouseUp, 0).add(LMouseDown, 0).add(LMouseUp, 0).build();
	}

	public Action leftDoubleClickAt(double absX, double absY){
		return leftDoubleClickAt(new AbsolutePosition(absX, absY));
	}

	public Action leftDoubleClickAt(Widget w){
		return leftDoubleClickAt(w, 0.5, 0.5);
	}

	public Action leftDoubleClickAt(Widget w, double relX, double relY){
		Finder wf = abstractor.apply(w);
		Action ret = leftDoubleClickAt(new WidgetPosition(wf, Tags.Shape, relX, relY, true));
		ret.set(Tags.Targets, Util.newArrayList(wf));
		ret.set(Tags.TargetID, w.get(Tags.ConcreteID));
		return ret;
	}

	public Action dropDownAt(Position position){
		Assert.notNull(position);
		
		return new CompoundAction.Builder().add(new MouseMove(position), 1)
				.add(LMouseDown, 0).add(LMouseUp, 0).add(NOP, 0.2).add(new KeyDown(KBKeys.VK_RIGHT),0).build();
	}
	
	public Action dropDownAt(double absX, double absY){
		return dropDownAt(new AbsolutePosition(absX, absY));
	}
	
	public Action dropDownAt(Widget w, double relX, double relY){
		Finder wf = abstractor.apply(w);
		Action ret = dropDownAt(new WidgetPosition(wf, Tags.Shape, relX, relY, true));
		ret.set(Tags.Targets, Util.newArrayList(wf));
		ret.set(Tags.TargetID, w.get(Tags.ConcreteID));
		return ret;
	}
	
	public Action dropDownAt(Widget w){
		return dropDownAt(w, 0.5, 0.5);
	}

	public Action dragFromTo(Widget from, Widget to){
		return dragFromTo(from, 0.5, 0.5, to, 0.5, 0.5);
	}

	public Action dragFromTo(Widget from, double fromRelX, double fromRelY, Widget to, double toRelX, double toRelY){
		return dragFromTo(new WidgetPosition(abstractor.apply(from), Tags.Shape, fromRelX, fromRelY, true),
				new WidgetPosition(abstractor.apply(to), Tags.Shape, toRelX, toRelY, true));
	}

	public Action dragFromTo(Position from, Position to){
		return new CompoundAction.Builder().add(new MouseMove(from), 1)
				.add(LMouseDown, 0).add(new MouseMove(to), 1)
				.add(LMouseUp, 0).build();		
	}
	
	public Action slideFromTo(Position from, Position to){
		Action action = dragFromTo(from,to);
		action.set(Tags.Slider, new Position[]{from,to});
		return action;
	}

	public Action slideFromTo(Position from, Position to, Widget widget){
		Action action = slideFromTo(from, to);
		action.set(Tags.OriginWidget, widget);
		return action;
	}

	/**
	 *
	 * @param w, widget that allows inserting text
	 * @param text, text to be inserted
	 * @param replaceText, true = replace, false = append
	 * @return Action that inserts text into the widget
	 */
	public Action clickTypeInto(Widget w, String text, boolean replaceText){
		return clickTypeInto(w, 0.5, 0.5, text, replaceText);
	}

	/**
	 * This could be private function?
	 *
	 * Translates the relative position of the click into absolute coordinates
	 *
	 * @param w
	 * @param relX
	 * @param relY
	 * @param text
	 * @param replaceText
	 * @return
	 */
	public Action clickTypeInto(Widget w, double relX, double relY, String text, boolean replaceText){
		Finder wf = abstractor.apply(w);
		Action ret = null;
		if(replaceText){
			ret = clickAndReplaceText(new WidgetPosition(wf, Tags.Shape, relX, relY, true), text);
		}else{
			ret = clickAndAppendText(new WidgetPosition(wf, Tags.Shape, relX, relY, true), text);
		}
		ret.set(Tags.Targets, Util.newArrayList(wf));
		ret.set(Tags.TargetID, w.get(Tags.ConcreteID));
		return ret;
	}

	public Action clickAndReplaceText(final Position position, final String text){
		Assert.notNull(position, text);
		// clicking the widget to select it:
		Builder builder = new CompoundAction.Builder().add(leftClickAt(position), 1);
		// pressing Cntr + A keys to select all text:
		builder.add(new KeyDown(KBKeys.VK_CONTROL), 0.1).add(new KeyDown(KBKeys.VK_A), 0.1).add(new KeyUp(KBKeys.VK_A), 0.1).add(new KeyUp(KBKeys.VK_CONTROL), 0.1);
		/*
		// old text replacements by pressing delete multiple times:
		final int TEXT_REMOVE_TRIES = 16; // VK_BACK_SPACE @web applications => back-history issue (pressing BACKSPACE) <- when? typing outside text-boxes
		//Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, false); // VK_SHIFT bug fix (did not work)
		Builder builder = new CompoundAction.Builder()
				.add(leftClickAt(position), 1)
				//.add(new KeyDown(KBKeys.VK_END), 1).add(new KeyUp(KBKeys.VK_END), 1)
				//.add(new KeyDown(KBKeys.VK_SHIFT), 1)
				.add(new KeyDown(KBKeys.VK_HOME), 1).add(new KeyUp(KBKeys.VK_HOME), 1);
		//.add(new KeyUp(KBKeys.VK_SHIFT), 1);
		for ( int i=0; i<TEXT_REMOVE_TRIES; i++)
			builder.add(new KeyDown(KBKeys.VK_DELETE), 1).add(new KeyUp(KBKeys.VK_DELETE), 1);
		*/
		if(text.length()==0){
			//empty string, pressing DEL
			builder.add(new KeyDown(KBKeys.VK_DELETE),0.1);
		}else {
			// Typing the new text:
			builder.add(new Type(text), 1);
		}
		return builder.build();
	}

	public Action clickAndAppendText(final Position position, final String text){
		Assert.notNull(position, text);
		// clicking the widget to select it:
		Builder builder = new CompoundAction.Builder().add(leftClickAt(position), 1);
		// pressing End key to append into the end of the text:
		builder.add(new KeyDown(KBKeys.VK_END), 0.1).add(new KeyUp(KBKeys.VK_END), 0.1);
		//inserting text:
		builder.add(new Type(text), 1);
		return builder.build();
	}
	
	/**
	 *
	 * @param w, widget that allows inserting text
	 * @param text, text to be pasted
	 * @param replaceText, true = replace, false = append
	 * @return Action that paste text into the widget
	 */
	public Action pasteTextInto(Widget w, String text, boolean replaceText){
		return pasteTextInto(w, 0.5, 0.5, text, replaceText);
	}

	public Action pasteTextInto(Widget w, double relX, double relY, String text, boolean replaceText){
		Finder wf = abstractor.apply(w);
		Action ret = null;
		if(replaceText){
			ret = pasteAndReplaceText(new WidgetPosition(wf, Tags.Shape, relX, relY, true), text);
		}else{
			ret = pasteAndAppendText(new WidgetPosition(wf, Tags.Shape, relX, relY, true), text);
		}
		ret.set(Tags.Targets, Util.newArrayList(wf));
		ret.set(Tags.TargetID, w.get(Tags.ConcreteID));
		ret.set(Tags.OriginWidget, w);
		return ret;
	}

	public Action pasteAndReplaceText(final Position position, final String text){
		Assert.notNull(position, text);
		// clicking the widget to select it:
		Builder builder = new CompoundAction.Builder().add(leftClickAt(position), 1);
		// pressing Cntr + A keys to select all text:
		builder.add(new KeyDown(KBKeys.VK_CONTROL), 0.1).add(new KeyDown(KBKeys.VK_A), 0.1).add(new KeyUp(KBKeys.VK_A), 0.1).add(new KeyUp(KBKeys.VK_CONTROL), 0.1);

		builder.add(new PasteText(text), 1);
		return builder.build();
	}

	public Action pasteAndAppendText(final Position position, final String text){
		Assert.notNull(position, text);
		// clicking the widget to select it:
		Builder builder = new CompoundAction.Builder().add(leftClickAt(position), 1);
		// pressing End key to append into the end of the text:
		builder.add(new KeyDown(KBKeys.VK_END), 0.1).add(new KeyUp(KBKeys.VK_END), 0.1);

		builder.add(new PasteText(text), 1);
		return builder.build();
	}

	public Action hitKey(KBKeys key) {
		return new CompoundAction.Builder().add(new KeyDown(key), .0)
				.add(new KeyUp(key), 1.0).add(NOP, 1.0).build();
	}
	
	public Action hitShortcutKey(List<KBKeys> keys) {
		if (keys.size() == 1) // single key
			return hitKey(keys.get(0));
		CompoundAction.Builder builder = new CompoundAction.Builder();
		for (int i = 0; i < keys.size(); i++)
			builder.add(new KeyDown(keys.get(i)), i == 0 ? .0 : .1);
		for (int i = keys.size() - 1; i >= 0; i--)
			builder.add(new KeyUp(keys.get(i)), i == keys.size() - 1 ? 1.0 : .0);
		builder.add(NOP, 1.0);
		return builder.build();
	}
	
	public Action killProcessByPID(long pid){ return killProcessByPID(pid, 0); }
	public Action killProcessByName(String name){ return killProcessByName(name, 0); }
	public Action killProcessByPID(long pid, double timeToWaitBeforeKilling){ return KillProcess.byPID(pid, timeToWaitBeforeKilling); }
	public Action killProcessByName(String name, double timeToWaitBeforeKilling){ return KillProcess.byName(name, timeToWaitBeforeKilling); }
	public Action activateSystem(){	return new ActivateSystem(); }
}
