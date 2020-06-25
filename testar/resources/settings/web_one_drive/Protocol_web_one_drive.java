/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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


import java.util.Set;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.KeyDown;
import org.fruit.alayer.actions.PasteText;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.Type;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.monkey.ConfigTags;
import org.fruit.alayer.Tags;
import es.upv.staq.testar.NativeLinker;
import org.testar.protocols.DesktopProtocol;

import static java.awt.event.KeyEvent.VK_2;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_ENTER;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Title;
import static org.fruit.alayer.Tags.Enabled;

/**
 * This protocol is using the default Windows accessibility API (Windows UI Automation API) to test Web applications.
 *
 * It also gives examples how to automate setting a username and password into a login screen.
 */
public class Protocol_web_one_drive extends DesktopProtocol {

	// platform: Windows10 -> we expect Mozilla Firefox or Microsoft Internet Explorer
	static final int BROWSER_IEXPLORER = 1;
	static final int BROWSER_FIREFOX = 2;
	static int browser; // BROWSER_*
	static Role webController, webText; // browser dependent
	static double browser_toolbar_filter;

	// check browser
	private void initBrowser(){
		browser = 0;
		webController = NativeLinker.getNativeRole("UIADataItem"); // just init with some value
		webText = NativeLinker.getNativeRole("UIAEdit"); // just init with some value
		String sutPath = settings().get(ConfigTags.SUTConnectorValue);
		if (sutPath.contains("iexplore.exe")){
			browser = BROWSER_IEXPLORER;
			webController = NativeLinker.getNativeRole("UIACustomControl");
			webText = NativeLinker.getNativeRole("UIAEdit");
		} else if (sutPath.contains("firefox")){
			browser = BROWSER_FIREFOX;
			webController = NativeLinker.getNativeRole("UIADataItem");
			webText = NativeLinker.getNativeRole("UIAText");
		}
	}

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	 @Override
	protected void beginSequence(SUT system, State state){

		super.beginSequence(system, state);

		Keyboard kb = AWTKeyboard.build();

		/**
		 * START Option 1:
		 * read the widgets of the current state and execute action based on them
		 */
		/*state = getState(system);

		for(Widget w :state) {
			if(w.get(Tags.Title,"").contains("Email, phone, or Skype")) {
				StdActionCompiler ac = new AnnotatingActionCompiler();
				Action a = ac.pasteTextInto(w, "testarhandson@gmail.com", true);
				executeAction(system, state, a);
			}
		}

		for(Widget w :state) {
			if(w.get(Tags.Title,"").contains("Next")) {
				Role role = w.get(Tags.Role, Roles.Widget);
				if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAButton")})) {
					StdActionCompiler ac = new AnnotatingActionCompiler();
					executeAction(system, state, ac.leftClickAt(w));
				}
			}
		}

		//Wait a bit
		Util.pause(5);

		//Update state
		state = getState(system);

		for(Widget w :state) {
			if(w.get(Tags.Title,"").contains("Enter the password")) {
				Role role = w.get(Tags.Role, Roles.Widget);
				if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAEdit")})) {
					StdActionCompiler ac = new AnnotatingActionCompiler();
					executeAction(system, state, ac.pasteTextInto(w, "0neDrivetestar", true));
				}
			}
		}

		for(Widget w :state) {
			if(w.get(Tags.Title,"").contains("Sign in")) {
				Role role = w.get(Tags.Role, Roles.Widget);
				if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAButton")})) {
					StdActionCompiler ac = new AnnotatingActionCompiler();
					executeAction(system, state, ac.leftClickAt(w));
				}
			}
		}

		//Wait a bit
		Util.pause(2);*/

		/**
		 * END Option 1
		 */


		/**
		 * START Option 2:
		 *  Work doing keyboard actions, without check the state and widgets
		 */
		new CompoundAction.Builder()   
		.add(new PasteText("testarhandson@gmail.com"),0.5) //assume keyboard focus is on the user field   
		.add(new KeyDown(VK_ENTER),0.5).build()
		.run(system, null, 1);

		Util.pause(8);

		new CompoundAction.Builder()
		.add(new PasteText("0neDrivetestar"),0.5)   
		.add(new KeyDown(VK_ENTER),0.5).build() //assume login is performed by ENTER 
		.run(system, null, 1);

		//Wait a bit
		Util.pause(1);

		/**
		 * END Option 2
		 */

		
		/**
		 * START Option 3: 
		 * Use TESTAR internal methods to find the desired widget Tag with the Value and execute actions like click or type
		 */
		
		/*if (waitLeftClickAndTypeIntoWidgetWithMatchingTag(Tags.Title, "Email, phone, or Skype", "testarhandson@gmail.com", state, system, 5, 1)) {
			System.out.println("Typing email credentials was sucessfull.");
		}
		
		if(waitAndLeftClickWidgetWithMatchingTag(Tags.Title, "Next", state, system, 5, 1)){
	 		System.out.println("Left click on Next button was successful.");
		}
		
		Util.pause(5);
		
		if (waitLeftClickAndTypeIntoWidgetWithMatchingTag(Tags.Title, "Enter the password", "0neDrivetestar", state, system, 5, 1)) {
			System.out.println("Typing password credentials was sucessfull.");
		}
		
		if(waitAndLeftClickWidgetWithMatchingTag(Tags.Title, "Sign in", state, system, 5, 1)){
	 		System.out.println("Left click on Sign in button was successful.");
		}
		
		Util.pause(2);*/
		
		/**
		 * END Option 3
		 */

	}

	/**
	 * This method is called when TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException{

		State state = super.getState(system);

		for(Widget w : state){
			Role role = w.get(Tags.Role, Roles.Widget);
			if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAToolBar")}))
				browser_toolbar_filter = w.get(Tags.Shape,null).y() + w.get(Tags.Shape,null).height();
		}

		return state;

	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state){

		Verdict verdict = super.getVerdict(state);
		// system crashes, non-responsiveness and suspicious titles automatically detected!

		//-----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
		//-----------------------------------------------------------------------------

		Role role;
		String title;
		Shape shape;

		// search all widgets for suspicious titles
		for(Widget w : state){

			role = w.get(Tags.Role, null);
			title = w.get(Title, "");
			shape = w.get(Tags.Shape, null);

			// Check that all images have an alternate textual description (accessibility, W3C WAI)
			verdict = verdict.join(getW3CWAIVerdict(state, w, role, title));

			// check for too small texts to be legible
			verdict = verdict.join(getSmallTextVerdict(state, w, role, shape));

			// check for scrollable UI elements whether their size is enough for usability
			verdict = verdict.join(getScrollsUsabilityVerdict(state, w, shape));

		}

		return verdict;

	}

	private Verdict getW3CWAIVerdict(State state, Widget w, Role role, String title){
		if (role != null && role.equals(NativeLinker.getNativeRole("UIAImage")) && title.isEmpty())
			return new Verdict(Verdict.SEVERITY_WARNING, "Not all images have an alternate textual description",
					new ShapeVisualizer(BluePen, w.get(Tags.Shape), "W3C WAI", 0.5, 0.5));
		else
			return Verdict.OK;
	}

	private Verdict getSmallTextVerdict(State state, Widget w,  Role role, Shape shape){
		final int MINIMUM_FONT_SIZE = 8; // px
		if (role != null && role.equals(NativeLinker.getNativeRole("UIAText")) && shape.height() < MINIMUM_FONT_SIZE)
			return new Verdict(Verdict.SEVERITY_WARNING, "Not all texts have a size greater than " + MINIMUM_FONT_SIZE + "px",
					new ShapeVisualizer(BluePen, w.get(Tags.Shape), "Too small text", 0.5, 0.5));
		else
			return Verdict.OK;
	}

	private Verdict getScrollsUsabilityVerdict(State state, Widget w, Shape shape){
		final int MINIMUM_SCROLLABLE_UISIZE = 24; // px
		try {
			if (NativeLinker.getNativeBooleanProperty(w, "UIAIsScrollPatternAvailable")){
				if (NativeLinker.getNativeBooleanProperty(w, "UIAVerticallyScrollable") && shape.height() < MINIMUM_SCROLLABLE_UISIZE)
					return new Verdict(Verdict.SEVERITY_WARNING, "Not all vertical-scrollable UI elements are greater than " + MINIMUM_SCROLLABLE_UISIZE + "px",
							new ShapeVisualizer(BluePen, w.get(Tags.Shape), "Too small vertical-scrollable UI element", 0.5, 0.5));
				if (NativeLinker.getNativeBooleanProperty(w, "UIAHorizontallyScrollable") && shape.width() < MINIMUM_SCROLLABLE_UISIZE)
					return new Verdict(Verdict.SEVERITY_WARNING, "Not all horizontal-scrollable UI elements are greater than " + MINIMUM_SCROLLABLE_UISIZE + "px",
							new ShapeVisualizer(BluePen, w.get(Tags.Shape), "Too small horizontal-scrollable UI element", 0.5, 0.5));
			}
		} catch (NoSuchTagException nste) { return Verdict.OK; }
		return Verdict.OK;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		Set<Action> actions = super.deriveActions(system,state);
		// unwanted processes, force SUT to foreground, ... actions automatically derived!

		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		//----------------------
		// BUILD CUSTOM ACTIONS
		//----------------------

		// iterate through all widgets
		for(Widget w : state){

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets

					// create left clicks
					if(whiteListed(w) || isClickable(w))
						actions.add(ac.leftClickAt(w));

					// create double left click
					if(whiteListed(w) || isDoubleClickable(w)){
						if(browser == BROWSER_FIREFOX)
							actions.add(ac.leftDoubleClickAt(w));
						else if (browser == BROWSER_IEXPLORER)
							actions.add(ac.dropDownAt(w));
					}

					// type into text boxes
					if(isTypeable(w)){
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					// slides
					addSlidingActions(actions,ac,SCROLL_ARROW_SIZE,SCROLL_THICK,w,state);

				}

			}

		}

		return actions;
	}

	private final int MAX_CLICKABLE_TITLE_LENGTH = 12;

	@Override
	protected boolean isClickable(Widget w){
		if (!isAtBrowserCanvas(w))
			return false;

		String title = w.get(Title, "");
		Role role = w.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, webText) && title.length() < MAX_CLICKABLE_TITLE_LENGTH)
			return super.isUnfiltered(w);
		else
			return super.isClickable(w);
	}

	private boolean isDoubleClickable(Widget w){
		if (!isAtBrowserCanvas(w))
			return false;

		if (isClickable(w)){
			Widget wParent = w.parent();
			if (wParent != null){
				Role roleP = wParent.get(Tags.Role, null);
				if (roleP != null && Role.isOneOf(roleP,webController))
					return isUnfiltered(w);
			}
		}

		return false;
	}

	@Override
	protected boolean isTypeable(Widget w){
		if (!isAtBrowserCanvas(w))
			return false;

		Role role = w.get(Tags.Role, null);
		if (role != null && Role.isOneOf(role, webText))
			return isUnfiltered(w);

		return false;
	}

	private boolean isAtBrowserCanvas(Widget w){
		Shape shape = w.get(Tags.Shape,null);
		if (shape != null && shape.y() > browser_toolbar_filter)
			return true;
		else
			return false;
	}

}
