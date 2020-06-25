/***************************************************************************************************
 *
 * Copyright (c) 2019, 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019, 2020 Open Universiteit - www.ou.nl
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


package org.testar.protocols;

import nl.ou.testar.HtmlReporting.HtmlSequenceReport;
import nl.ou.testar.RandomActionSelector;
import org.fruit.Environment;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.monkey.ConfigTags;
import org.testar.OutputStructure;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class DesktopProtocol extends GenericUtilsProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness
    protected HtmlSequenceReport htmlReport;
    protected State latestState;

    /**
     * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void preSequencePreparations() {
    	
    	if(!new File(settings.get(ConfigTags.InputFileText)).exists()) {
    		System.out.println("Warning: BLNS file from "+ settings.get(ConfigTags.ProtocolClass) + " settings cannot be readed, "
    				+ "check if the current value is correct: " + settings.get(ConfigTags.InputFileText));
    		System.out.println("Example of correct value: \"InputFileText = ./settings/desktop_generic/blns.txt\" ");
    		System.out.println("If you want to use a set of advanced Strings to test the text input fields, "
    				+ "please configure properly the InputFileText setting");
    	}
    	
        //initializing the HTML sequence report:
        htmlReport = new HtmlSequenceReport();
    }
    
    /**
     * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
     * This can be used for example for bypassing a login screen by filling the username and password
     * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuration files etc.)
     */
    @Override
    protected void beginSequence(SUT system, State state) {
    	super.beginSequence(system, state);
    	
    	double displayScale = Environment.getInstance().getDisplayScale(state.child(0).get(Tags.HWND, (long)0));
    	
    	mouse.setCursorDisplayScale(displayScale);
    }

    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine. The state should have attached an oracle
     * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
     * state is erroneous and if so why.
     * @return  the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {
        //Spy mode didn't use the html report
    	if(settings.get(ConfigTags.Mode) == Modes.Spy)
        	return super.getState(system);
    	
    	latestState = super.getState(system);
        //adding state to the HTML sequence report:
        htmlReport.addState(latestState);
        return latestState;
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
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

        //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        //the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        return super.deriveActions(system,state);
    }

    /**
     * Overwriting to add HTML report writing into it
     *
     * @param state
     * @param actions
     * @return
     */
    @Override
    protected Action preSelectAction(State state, Set<Action> actions){
        // adding available actions into the HTML report:
        htmlReport.addActions(actions);
        return(super.preSelectAction(state, actions));
    }

    /**
     * Select one of the available actions (e.g. at random)
     * @param state the SUT's current state
     * @param actions the set of derived actions
     * @return  the selected action (non-null!)
     */
    @Override
    protected Action selectAction(State state, Set<Action> actions){
        //Call the preSelectAction method from the DefaultProtocol so that, if necessary,
        //unwanted processes are killed and SUT is put into foreground.
        Action retAction = preSelectAction(state, actions);
        if (retAction == null)
            //if no preSelected actions are needed, then implement your own strategy
            retAction = RandomActionSelector.selectAction(actions);
        return retAction;
    }

    /**
     * Execute the selected action.
     * @param system the SUT
     * @param state the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    @Override
    protected boolean executeAction(SUT system, State state, Action action){
        // adding the action that is going to be executed into HTML report:
        htmlReport.addSelectedAction(state, action);
        return super.executeAction(system, state, action);
    }

    /**
     * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void postSequenceProcessing() {
        htmlReport.addTestVerdict(getVerdict(latestState).join(processVerdict));
        
        String sequencesPath = getGeneratedSequenceName();
        try {
        	sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
        }catch (Exception e) {}
        		
        String status = (getVerdict(latestState).join(processVerdict)).verdictSeverityTitle();
		String statusInfo = (getVerdict(latestState).join(processVerdict)).info();
		
		statusInfo = statusInfo.replace("\n"+Verdict.OK.info(), "");
		
		//Timestamp(generated by logback.xml) SUTname Mode SequenceFileObject Status "StatusInfo"
		INDEXLOG.info(OutputStructure.executedSUTname
				+ " " + settings.get(ConfigTags.Mode, mode())
				+ " " + sequencesPath
				+ " " + status + " \"" + statusInfo + "\"" );
		
		//Print into command line the result of the execution, useful to work with CI and timestamps
		System.out.println(OutputStructure.executedSUTname
				+ " " + settings.get(ConfigTags.Mode, mode())
				+ " " + sequencesPath
				+ " " + status + " \"" + statusInfo + "\"" );
    }

    /**
     * Iterating through all widgets of the given state
     *
     * Adding derived actions into the given set of actions and returning the modified set of actions.
     *
     * @param actions
     * @param system
     * @param state
     * @return
     */
    protected Set<Action> deriveClickTypeScrollActionsFromAllWidgetsOfState(Set<Action> actions, SUT system, State state){
        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
        for(Widget w : state){
            //optional: iterate through top level widgets based on Z-index:
            //for(Widget w : getTopWidgets(state)){

            if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
                // filtering out actions on menu-containers (that would add an action in the middle of the menu)
                continue; // skip this iteration of the for-loop
            }

            // Only consider enabled and non-blocked widgets
            if(w.get(Enabled, true) && !w.get(Blocked, false)){

                // Do not build actions for widgets on the blacklist
                // The blackListed widgets are those that have been filtered during the SPY mode with the
                //CAPS_LOCK + SHIFT + Click clickfilter functionality.
                if (!blackListed(w)){

                    //For widgets that are:
                    // - clickable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of left clicking on them
                    if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a left click action with the Action Compiler, and add it to the set of derived actions
                        actions.add(ac.leftClickAt(w));
                    }

                    //For widgets that are:
                    // - typeable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of typing into them
                    if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions
                        final Optional<String[]> textList = Optional.ofNullable(getTextInputsFromFile(settings.get(ConfigTags.InputFileText)));
                        
                        //final String textToInsert = textList.isPresent() ? textList.get()[new Random().nextInt(textList.get().length)] : this.getRandomText(w);
                        final Action typeAction = textList.isPresent() ?
                        		ac.pasteTextInto(w, textList.get()[new Random().nextInt(textList.get().length)], true) :
                        			ac.clickTypeInto(w, this.getRandomText(w), true);
                        
                        actions.add(typeAction);
                    }
                    //Add sliding actions (like scroll, drag and drop) to the derived actions
                    //method defined below.
                    addSlidingActions(actions,ac,SCROLL_ARROW_SIZE,SCROLL_THICK,w, state);
                }
            }
        }
        return actions;
    }


    /**
     * Adding derived actions into the given set of actions and returning the modified set of actions.
     *
     * @param actions
     * @param system
     * @param state
     * @return
     */
    protected Set<Action> deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, SUT system, State state){
        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
        for(Widget w : getTopWidgets(state)){
            //optional: iterate through top level widgets based on Z-index:
            //for(Widget w : getTopWidgets(state)){

            if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
                // filtering out actions on menu-containers (that would add an action in the middle of the menu)
                continue; // skip this iteration of the for-loop
            }

            // Only consider enabled and non-blocked widgets
            if(w.get(Enabled, true) && !w.get(Blocked, false)){

                // Do not build actions for widgets on the blacklist
                // The blackListed widgets are those that have been filtered during the SPY mode with the
                //CAPS_LOCK + SHIFT + Click clickfilter functionality.
                if (!blackListed(w)){

                    //For widgets that are:
                    // - clickable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of left clicking on them
                    if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a left click action with the Action Compiler, and add it to the set of derived actions
                        actions.add(ac.leftClickAt(w));
                    }

                    //For widgets that are:
                    // - typeable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of typing into them
                    if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions
                        final Optional<String[]> textList = Optional.ofNullable(getTextInputsFromFile(settings.get(ConfigTags.InputFileText)));
                        
                        //final String textToInsert = textList.isPresent() ? textList.get()[new Random().nextInt(textList.get().length)] : this.getRandomText(w);
                        final Action typeAction = textList.isPresent() ?
                        		ac.pasteTextInto(w, textList.get()[new Random().nextInt(textList.get().length)], true) :
                        			ac.clickTypeInto(w, this.getRandomText(w), true);
                        
                        actions.add(typeAction);
                    }
                    //Add sliding actions (like scroll, drag and drop) to the derived actions
                    //method defined below.
                    addSlidingActions(actions,ac,SCROLL_ARROW_SIZE,SCROLL_THICK,w, state);
                }
            }
        }
        return actions;
    }

    private String[] getTextInputsFromFile(final String inputFile) {
        try (final Stream<String> lines = Files.lines(new File(inputFile).toPath())) {
            return lines.filter(line -> !line.startsWith("#") && !line.isEmpty()).toArray(String[]::new);
        } catch (IOException e) {
            return null;
        }
    }
}
