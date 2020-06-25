/***************************************************************************************************
 *
 * Copyright (c) 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
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
import org.fruit.alayer.Action;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.Tags;
import org.testar.protocols.DesktopProtocol;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

//TODO Fernando: create a higher level SwingProtocol and document this one after that
/**
 * Protocol specifically for testing Java Swing applications.
 */
public class Protocol_desktop_SwingSet2 extends DesktopProtocol {


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

    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

        Set<Action> actions = super.deriveActions(system,state);
        // unwanted processes, force SUT to foreground, ... actions automatically derived!

        // create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();

        //----------------------
        // BUILD CUSTOM ACTIONS
        //----------------------

        // iterate through all widgets
        for(Widget w : getTopWidgets(state)){

            if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

                if (!blackListed(w)){  // do not build actions for tabu widgets

                    // left clicks
                    if(whiteListed(w) || isClickable(w))
                        actions.add(ac.leftClickAt(w));

                    // type into text boxes
                    if(isTypeable(w))
                        actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));

                    //Force actions on some widgets with a wrong accessibility
                    //Optional, comment this changes if your Swing applications doesn't need it

                    if(w.get(Tags.Role).toString().contains("Tree") ||
                            w.get(Tags.Role).toString().contains("ComboBox") ||
                            w.get(Tags.Role).toString().contains("List")) {
                        widgetTree(w, actions);
                    }
                    //End of Force action

                }

            }

        }

        return actions;

    }

    //Force actions on Tree widgets with a wrong accessibility
    public void widgetTree(Widget w, Set<Action> actions) {
        StdActionCompiler ac = new AnnotatingActionCompiler();
        actions.add(ac.leftClickAt(w));
        w.set(Tags.ActionSet, actions);
        for(int i = 0; i<w.childCount(); i++) {
            widgetTree(w.child(i), actions);
        }
    }
}
