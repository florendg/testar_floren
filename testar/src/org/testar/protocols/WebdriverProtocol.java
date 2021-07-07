/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2021 Open Universiteit - www.ou.nl
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

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.fruit.Environment;
import org.fruit.Pair;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.WdDriver;
import org.fruit.alayer.webdriver.WdElement;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.alayer.windows.Windows;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.OutputStructure;

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.HtmlReporting.Reporting;

public class WebdriverProtocol extends GenericUtilsProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness
    protected Reporting htmlReport;
    protected State latestState;
    
    protected static Set<String> existingCssClasses = new HashSet<>();

	// WedDriver settings from file:
	protected List<String> clickableClasses, deniedExtensions, domainsAllowed;

	// URL + form name, username input id + value, password input id + value
	// Set login to null to disable this feature
	//TODO web driver settings for login feature
	protected Pair<String, String> login = Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
	protected Pair<String, String> username = Pair.from("username", "");
	protected Pair<String, String> password = Pair.from("password", "");

	// List of atributes to identify and close policy popups
	// Set to null to disable this feature
	@SuppressWarnings("serial")
	protected Map<String, String> policyAttributes = new HashMap<String, String>()
	{
		{ 
			put("id", "_cookieDisplay_WAR_corpcookieportlet_okButton");
		}
	};

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		// Indicate to TESTAR we want to use webdriver package implementation
		NativeLinker.addWdDriverOS();

		// reads the settings from file:
		super.initialize(settings);

		// Classes that are deemed clickable by the web framework
		clickableClasses = settings.get(ConfigTags.ClickableClasses);

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		deniedExtensions = settings.get(ConfigTags.DeniedExtensions).contains("null") ? null : settings.get(ConfigTags.DeniedExtensions);

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		domainsAllowed = settings.get(ConfigTags.DomainsAllowed).contains("null") ? null : settings.get(ConfigTags.DomainsAllowed);

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		WdDriver.followLinks = settings.get(ConfigTags.FollowLinks);

		//Force the browser to run in full screen mode
		WdDriver.fullScreen = settings.get(ConfigTags.BrowserFullScreen);

		//Force webdriver to switch to a new tab if opened
		//This feature can block the correct display of select dropdown elements 
		WdDriver.forceActivateTab = settings.get(ConfigTags.SwitchNewTabs);
	}
	
    /**
     * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void preSequencePreparations() {
        //initializing the HTML sequence report:
        htmlReport = getReporter();
    }
    
    /**
     * This method is called when TESTAR starts the System Under Test (SUT). The method should
     * take care of
     * 1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
     * out what executable to run)
     * 2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuratio files etc.)
     * 3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
     * seconds until they have finished loading)
     *
     * @return a started SUT, ready to be tested.
     */
    @Override
    protected SUT startSystem() throws SystemStartException {
    	// Add the domain from the SUTConnectorValue to domainsAllowed List
    	ensureDomainsAllowed();
    	
    	SUT sut = super.startSystem();

    	// A workaround to obtain the browsers window handle, ideally this information is acquired when starting the
    	// webdriver in the constructor of WdDriver.
    	// A possible solution could be creating a snapshot of the running browser processes before and after
    	if(System.getProperty("os.name").contains("Windows 10")
    			&& sut.get(Tags.HWND, null) == null) {
    		// Note don't place a breakpoint here since the outcome of the function call will result in the IDE pid and
    		// window handle. The running browser needs to be in the foreground when we reach this part.
    		long hwnd = Windows.GetForegroundWindow();
    		long pid = Windows.GetWindowProcessId(Windows.GetForegroundWindow());
    		// Safe to set breakpoints again.
    		if (WinProcess.procName(pid).contains("chrome")) {
    			sut.set(Tags.HWND, hwnd);
    			sut.set(Tags.PID, pid);
    			System.out.printf("INFO System PID %d and window handle %d have been set\n", pid, hwnd);
    		}
    	}

		double displayScale = getDisplayScale(sut);

		// See remarks in WdMouse
        mouse = sut.get(Tags.StandardMouse);
        mouse.setCursorDisplayScale(displayScale);

    	return sut;
    }

	/**
	 * Returns the display scale based on the settings, if the user has set the override webdriver display scale
	 * we return the override value otherwise the display scale obtained from the system.
	 * @param sut The system under test
	 * @return The display scale.
	 */
	private double getDisplayScale(SUT sut) {
		double displayScale = Environment.getInstance().getDisplayScale(sut.get(Tags.HWND, (long)0));

		// If the user has specified a scale override the display scale obtained from the system.
		String overrideDisplayScaleAsString = settings().get(ConfigTags.OverrideWebDriverDisplayScale, "");
		if (!overrideDisplayScaleAsString.isEmpty()) {
			try {
				double webDriverDisplayScaleOverride = Double.parseDouble(overrideDisplayScaleAsString);
				if (webDriverDisplayScaleOverride != 0) {
					displayScale = webDriverDisplayScaleOverride;
				}
			} catch (NumberFormatException nfe) {
				System.out.printf("WARNING Unable to convert display scale override to double: %s, will use %f\n", overrideDisplayScaleAsString, displayScale);
			}
		}
		return displayScale;
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
    	
    	try {
    		WdDriver.waitDocumentReady();
    	} catch(org.openqa.selenium.WebDriverException wde) {
    		LogSerialiser.log("WEBDRIVER ERROR: Selenium Chromedriver seems not to respond!\n", LogSerialiser.LogLevel.Critical);
    		System.out.println("******************************************************************");
    		System.out.println("** WEBDRIVER ERROR: Selenium Chromedriver seems not to respond! **");
    		System.out.println("******************************************************************");
    		System.out.println(wde.getMessage());
    		system.set(Tags.IsRunning, false);
    	}

    	State state = super.getState(system);

    	if(settings.get(ConfigTags.ForceForeground)
    			&& System.getProperty("os.name").contains("Windows 10")
    			&& system.get(Tags.IsRunning, false) && !system.get(Tags.NotResponding, false)
    			&& system.get(Tags.PID, (long)-1) != (long)-1 
    			&& WinProcess.procName(system.get(Tags.PID)).contains("chrome") 
    			&& !WinProcess.isForeground(system.get(Tags.PID))){
    		
    		WinProcess.politelyToForeground(system.get(Tags.HWND));
    		LogSerialiser.log("Trying to set Chrome Browser to Foreground... " 
    		+ WinProcess.procName(system.get(Tags.PID)) + "\n");
    		
    	}

    	latestState = state;
    	
    	//Spy mode didn't use the html report
    	if(settings.get(ConfigTags.Mode) == Modes.Spy) {

    		for(Widget w : state) {
    			WdElement element = ((WdWidget) w).element;
    			for(String s : element.cssClasses) {
    				existingCssClasses.add(s);
    			}
    		}
    		
        	return state;
    	}
    	
        //adding state to the HTML sequence report:
        htmlReport.addState(latestState);
        return latestState;
    }
    
	/**
	 * Select one of the possible actions (e.g. at random)
	 *
	 * @param state   the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		// Derive actions didn't find any action, inform the user and force WdHistoryBackAction
		if(actions == null || actions.isEmpty()) {
			System.out.println(String.format("** WEBDRIVER WARNING: In Action number %s the State seems to have no interactive widgets", actionCount()));
			System.out.println(String.format("** URL: %s", WdDriver.getCurrentUrl()));
			System.out.println("** Please try to navigate with SPY mode and configure clickableClasses inside Java protocol");
			actions = new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		}
		
		return super.selectAction(state, actions);
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
     * Replay the saved action
     */
    @Override
    protected boolean replayAction(SUT system, State state, Action action, double actionWaitTime, double actionDuration){
        // adding the action that is going to be executed into HTML report:
        htmlReport.addSelectedAction(state, action);
        return super.replayAction(system, state, action, actionWaitTime, actionDuration);
    }

    /**
     * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void postSequenceProcessing() {
        String status = "";
        String statusInfo = "";

        if(mode() == Modes.Replay) {
            htmlReport.addTestVerdict(getReplayVerdict().join(processVerdict));
            status = (getReplayVerdict().join(processVerdict)).verdictSeverityTitle();
            statusInfo = (getReplayVerdict().join(processVerdict)).info();
        }
        else {
            htmlReport.addTestVerdict(getVerdict(latestState).join(processVerdict));
            status = (getVerdict(latestState).join(processVerdict)).verdictSeverityTitle();
            statusInfo = (getVerdict(latestState).join(processVerdict)).info();
        }

        String sequencesPath = getGeneratedSequenceName();
        try {
            sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
        }catch (Exception e) {}

        statusInfo = statusInfo.replace("\n"+Verdict.OK.info(), "");

        //Timestamp(generated by logger) SUTname Mode SequenceFileObject Status "StatusInfo"
        INDEXLOG.info(OutputStructure.executedSUTname
                + " " + settings.get(ConfigTags.Mode, mode())
                + " " + sequencesPath
                + " " + status + " \"" + statusInfo + "\"" );

        htmlReport.close();
    }
    
    @Override
	protected void finishSequence(){
		//With webdriver version we don't use the call SystemProcessHandling.killTestLaunchedProcesses
	}

    @Override
    protected void stopSystem(SUT system) {
        if(settings.get(ConfigTags.Mode) == Modes.Spy) {

            try {
                if(Settings.getSettingsPath() != null) {
                    File folder = new File(Settings.getSettingsPath());
                    File file = new File(folder, "existingCssClasses.txt");
                    if(!file.exists())
                        file.createNewFile();

                    Stream<String> stream = Files.lines(Paths.get(file.getCanonicalPath()));
                    stream.forEach(line -> existingCssClasses.add(line));
                    stream.close();

                    PrintWriter write = new PrintWriter(new FileWriter(file.getCanonicalPath()));
                    for(String s : existingCssClasses)
                        write.println(s);
                    write.close();
                }

            } catch (Exception e) {System.out.println(e.getMessage());}
        }

        super.stopSystem(system);
    }
    
    @Override
	protected void closeTestSession() {
		super.closeTestSession();
		NativeLinker.cleanWdDriverOS();
	}

	/*
	 * Check the state if we need to force an action
	 */
	protected Set<Action> detectForcedActions(State state, StdActionCompiler ac) {
		Set<Action> actions = detectForcedDeniedUrl();
		if (actions != null && actions.size() > 0) {
			return actions;
		}

		actions = detectForcedLogin(state);
		if (actions != null && actions.size() > 0) {
			return actions;
		}

		actions = detectForcedPopupClick(state, ac);
		if (actions != null && actions.size() > 0) {
			return actions;
		}

		return null;
	}

	/*
	 * Detect and perform login if defined
	 */
	protected Set<Action> detectForcedLogin(State state) {
		if (login == null || username == null || password == null) {
			return null;
		}

		// Check if the current page is a login page
		String currentUrl = WdDriver.getCurrentUrl();
		if (currentUrl.startsWith(login.left())) {
			CompoundAction.Builder builder = new CompoundAction.Builder();
			// Set username and password
			for (Widget widget : state) {
				WdWidget wdWidget = (WdWidget) widget;
				// Only enabled, visible widgets
				if (!widget.get(Enabled, true) || widget.get(Blocked, false)) {
					continue;
				}

				if (username.left().equals(wdWidget.getAttribute("id"))) {
					builder.add(new WdAttributeAction(
							username.left(), "value", username.right()), 1);
				}
				else if (password.left().equals(wdWidget.getAttribute("id"))) {
					builder.add(new WdAttributeAction(
							password.left(), "value", password.right()), 1);
				}
			}
			// Submit form, but only if user and pass are filled
			builder.add(new WdSubmitAction(login.right()), 2);
			CompoundAction actions = builder.build();
			if (actions.getActions().size() >= 3) {
				return new HashSet<>(Collections.singletonList(actions));
			}
		}

		return null;
	}

	/*
	 * Force closing of Policies Popup
	 */
	protected Set<Action> detectForcedPopupClick(State state, StdActionCompiler ac) {
		if (policyAttributes == null || policyAttributes.size() == 0) {
			return null;
		}

		for (Widget widget : state) {
			// Only enabled, visible widgets
			if (!widget.get(Enabled, true) || widget.get(Blocked, false)) {
				continue;
			}

			WdElement element = ((WdWidget) widget).element;
			boolean isPopup = true;
			for (Map.Entry<String, String> entry : policyAttributes.entrySet()) {
				String attribute = element.attributeMap.get(entry.getKey());
				isPopup &= entry.getValue().equals(attribute);
			}
			if (isPopup) {
				return new HashSet<>(Collections.singletonList(clickForcedPopupWidget(ac, widget)));
			}
		}

		return null;
	}

	/**
	 * By default click popup widget using LeftClickAt (Windows level). 
	 * 
	 * @param ac
	 * @param widget
	 * @return leftClickAt(widget)
	 */
	protected Action clickForcedPopupWidget(StdActionCompiler ac, Widget widget) {
	    return ac.leftClickAt(widget);
	}

	/*
	 * Force back action due to disallowed domain or extension
	 */
	protected Set<Action> detectForcedDeniedUrl() {
		String currentUrl = WdDriver.getCurrentUrl();

		// Don't get caught in PDFs etc. and non-whitelisted domains
		if (isUrlDenied(currentUrl) || isExtensionDenied(currentUrl)) {
			// If opened in new tab, close it
			if (WdDriver.getWindowHandles().size() > 1) {
				return new HashSet<>(Collections.singletonList(new WdCloseTabAction()));
			}
			// Single tab, go back to previous page
			else {
				return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
			}
		}

		return null;
	}

	/*
	 * Check if the current address has a denied extension (PDF etc.)
	 */
	protected boolean isExtensionDenied(String currentUrl) {
		// If the current page doesn't have an extension, always allow
		if (!currentUrl.contains(".")) {
			return false;
		}

		if (deniedExtensions == null || deniedExtensions.size() == 0) {
			return false;
		}

		// Deny if the extension is in the list
		String ext = currentUrl.substring(currentUrl.lastIndexOf(".") + 1);
		ext = ext.replace("/", "").toLowerCase();
		return deniedExtensions.contains(ext);
	}

	/*
	 * Check if the URL is denied
	 */
	protected boolean isUrlDenied(String currentUrl) {
		if (currentUrl.startsWith("mailto:")) {
			return true;
		}

		// Always allow local file
		if (currentUrl.startsWith("file:///")) {
			return false;
		}

		// User wants to allow all
		if (domainsAllowed == null) {
			return false;
		}

		// Only allow pre-approved domains
		String domain = getDomain(currentUrl);
		return !domainsAllowed.contains(domain);
	}

	/*
	 * Check if the widget has a denied URL as hyperlink
	 */
	protected boolean isLinkDenied(Widget widget) {
		String linkUrl = widget.get(Tags.ValuePattern, "");

		// Not a link or local file, allow
		if (linkUrl == null || linkUrl.startsWith("file:///")) {
			return false;
		}

		// Deny the link based on extension
		if (isExtensionDenied(linkUrl)) {
			return true;
		}

		// Mail link, deny
		if (linkUrl.startsWith("mailto:")) {
			return true;
		}

		// Not a web link (or link to the same domain), allow
		if (!(linkUrl.startsWith("https://") || linkUrl.startsWith("http://"))) {
			return false;
		}

		// User wants to allow all
		if (domainsAllowed == null) {
			return false;
		}

		// Only allow pre-approved domains if
		String domain = getDomain(linkUrl);
		return !domainsAllowed.contains(domain);
	}

	/*
	 * Get the domain from a full URL
	 */
	protected String getDomain(String url) {
		if (url == null) {
			return null;
		}

		// When serving from file, 'domain' is filesystem
		if (url.startsWith("file://")) {
			return "file://";
		}

		url = url.replace("https://", "").replace("http://", "").replace("file://", "");
		return (url.split("/")[0]).split("\\?")[0];
	}

	/*
	 * If domainsAllowed from SUTConnectorValue is not set, include it in the domainsAllowed
	 */
	protected void ensureDomainsAllowed() {
		String[] parts = settings().get(ConfigTags.SUTConnectorValue).split(" ");
		String url = parts[parts.length - 1].replace("\"", "");

		try{
			if(domainsAllowed != null && !domainsAllowed.contains(getDomain(url))) {
				System.out.println(String.format("WEBDRIVER INFO: Automatically adding initial %s domain to domainsAllowed List", getDomain(url)));
				String[] newDomainsAllowed = domainsAllowed.stream().toArray(String[]::new);
				domainsAllowed = Arrays.asList(ArrayUtils.insert(newDomainsAllowed.length, newDomainsAllowed, getDomain(url)));
				System.out.println(String.format("domainsAllowed: %s", String.join(",", domainsAllowed)));
			}
		} catch(Exception e) {
			System.out.println("WEBDRIVER ERROR: Trying to add the startup domain to domainsAllowed List");
			System.out.println("Please review domainsAllowed List inside Webdriver Java Protocol");
		}
	}
	
	/*
	 * We need to check if click position is within the canvas
	 */
	protected boolean isAtBrowserCanvas(Widget widget) {
		Shape shape = widget.get(Tags.Shape, null);
		if (shape == null) {
			return false;
		}

		// Widget must be completely visible on viewport for screenshots
		return widget.get(WdTags.WebIsFullOnScreen, false);
	}
}
