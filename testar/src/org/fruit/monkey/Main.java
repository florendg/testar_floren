/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
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

package org.fruit.monkey;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.OperatingSystems;
import es.upv.staq.testar.StateManagementTags;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import org.fruit.*;
import org.fruit.alayer.Tag;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.windows.Windows10;
import org.testar.settings.ExtendedSettingFile;
import org.testar.settings.ExtendedSettingsFactory;

import static org.fruit.Util.compileProtocol;
import static org.fruit.monkey.ConfigTags.*;

public class Main {

	//public static final String TESTAR_DIR_PROPERTY = "DIRNAME"; //Use the OS environment to obtain TESTAR directory
	public static final String SETTINGS_FILE = "test.settings";
	public static final String SUT_SETTINGS_EXT = ".sse";
	public static String SSE_ACTIVATED = null;

	//Default paths
	public static String testarDir = "." + File.separator;
	public static String settingsDir = testarDir + "settings" + File.separator;
	public static String outputDir = testarDir + "output" + File.separator;
	public static String tempDir = outputDir + "temp" + File.separator;


	/**
	 * This method scans the settings directory of TESTAR for a file that end with extension SUT_SETTINGS_EXT
	 * @return A list of file names that have extension SUT_SETTINGS_EXT
	 */
	public static String[] getSSE() {
		return new File(settingsDir).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(SUT_SETTINGS_EXT);
			}
		});
	}

	/**
	 * According to the TESTAR directory and SSE file (settings and protocol to run)
	 * return the path of the selected settings
	 * 
	 * @return test.settings path
	 */
	public static String getTestSettingsFile() {
		return settingsDir + SSE_ACTIVATED + File.separator + SETTINGS_FILE;
	}

	/**
	 * Main method to run TESTAR
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		isValidJavaEnvironment();
		
		verifyTestarInitialDirectory();

		initTestarSSE(args);

		String testSettingsFileName = getTestSettingsFile();
		System.out.println("Test settings is <" + testSettingsFileName + ">");

		Settings settings = loadTestarSettings(args, testSettingsFileName);

		// Continuous Integration: If GUI is disabled TESTAR was executed from command line.
		// We only want to execute TESTAR one time with the selected settings.
		if(!settings.get(ConfigTags.ShowVisualSettingsDialogOnStartup)){

			setTestarDirectory(settings);

			initCodingManager(settings);

			initOperatingSystem();

			startTestar(settings);
		}

		//TESTAR GUI is enabled, we're going to show again the GUI when the selected protocol execution finishes
		else{
			while(startTestarDialog(settings, testSettingsFileName)) {

				testSettingsFileName = getTestSettingsFile();
				settings = loadTestarSettings(args, testSettingsFileName);

				setTestarDirectory(settings);

				initCodingManager(settings);

				initOperatingSystem();

				startTestar(settings);
			}
		}

		TestSerialiser.exit();
		ScreenshotSerialiser.exit();
		LogSerialiser.exit();

		System.exit(0);

	}

	private static void isValidJavaEnvironment() {
		try {
			boolean javacExistsInPath = false;
			for(String path: System.getenv("PATH").split(File.pathSeparator)) {
				if(new File(path, "javac").exists()) {
					javacExistsInPath = true;
				}
			}
			if(! javacExistsInPath)
				System.out.println("PATH does not contain a Java Development Kit");

			if(!System.getenv("JAVA_HOME").contains("jdk"))
				System.out.println("JAVA HOME is not properly aiming to the Java Development Kit");

			if(!(System.getenv("JAVA_HOME").contains("1.8") || (System.getenv("JAVA_HOME").contains("-8"))))
				System.out.println("Java version is not JDK 1.8. " +
						"This software is not tested with your java version, use at your own risk.");
		}catch(Exception e) {
			System.out.println("Exception: Something is wrong with your JAVA_HOME \n"
				+"Check if JAVA_HOME system variable is correctly defined \n \n"
				+"GO TO: https://testar.org/wiki/Main_Page#FAQ to obtain more details \n \n");
		}
	}
	
	/**
	 * Verify the initial directory of TESTAR
	 * If this directory didn't contain testar.bat file inform the user
	 */
	private static void verifyTestarInitialDirectory() {
		// Obtain Files name of current testarDir
		Set<String> filesName = new HashSet<>();
		File[] filesList = new File(testarDir).listFiles();
        for(File file : filesList){
        	filesName.add(file.getName());
        }

        // Verify if we are in the correct executable testar directory (contains testar.bat)
		if(!filesName.contains("testar.bat")) {
			System.out.println("WARNING: We cannot find testar.bat executable file.");
			System.out.println("WARNING: Please change to /testar/bin/ folder (contains testar.bat) and try to execute again.");
			System.out.println(String.format("WARNING: Current directory %s with existing files:", new File(testarDir).getAbsolutePath()));
			filesName.forEach(System.out::println);
		}
	}

	/**
	 * Set the current directory of TESTAR, settings and output folders
	 */
	private static void setTestarDirectory(Settings settings) {
		//Use the OS environment to obtain TESTAR directory
		/*try {
			testarDir = System.getenv(TESTAR_DIR_PROPERTY);
		}catch (Exception e) {
			testarDir = "." + File.separator;
			System.out.println(e);
			System.out.println("Please execute TESTAR from their existing directory");
		}*/

		outputDir = settings.get(ConfigTags.OutputDir);
		tempDir = settings.get(ConfigTags.TempDir);
	}

	/**
	 * Find or create the .sse file, to known with what settings and protocol start TESTAR
	 * 
	 * @param args
	 */
	private static void initTestarSSE(String[] args){

		Locale.setDefault(Locale.ENGLISH);

		// TODO: put the code below into separate method/class
		// Get the files with SUT_SETTINGS_EXT extension and check whether it is not empty
		// and that there is exactly one.

		//Allow users to use command line to choose a protocol modifying sse file
		for(String sett : args) {
			if(sett.toString().contains("sse="))
				try {
					protocolFromCmd(sett);
				}catch(Exception e) {System.out.println("Error trying to modify sse from command line");}
		}

		String[] files = getSSE();

		// If there is more than 1, then delete them all
		if (files != null && files.length > 1) {
			System.out.println("Too many *.sse files - exactly one expected!");
			for (String f : files) {
				System.out.println("Delete file <" + f + "> = " + new File(f).delete());
			}
			files = null;
		}

		//If there is none, then start up a selection menu
		if (files == null || files.length == 0) {
			settingsSelection();
			if (SSE_ACTIVATED == null) {
				System.exit(-1);
			}
		}
		else {
			//Use the only file that was found
			SSE_ACTIVATED = files[0].split(SUT_SETTINGS_EXT)[0];
		}
	}

	/**
	 *  This method creates the dropdown menu to select a protocol when TESTAR starts WITHOUT a .sse file
	 */
	private static void settingsSelection() {

		Set<String> sutSettings = new HashSet<String>();
		for (File f : new File(settingsDir).listFiles()) {
			if (new File(f.getPath() + File.separator + SETTINGS_FILE).exists()) {
				sutSettings.add(f.getName());
			}
		}

		if (sutSettings.isEmpty()) {
			System.out.println("No SUT settings found!");
		}

		else {
			Object[] options = sutSettings.toArray();
			Arrays.sort(options);
			JFrame settingsSelectorDialog = new JFrame();
			settingsSelectorDialog.setAlwaysOnTop(true);
			String sseSelected = (String) JOptionPane.showInputDialog(settingsSelectorDialog,
					"Select the desired setting:", "TESTAR settings", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (sseSelected == null) {
				SSE_ACTIVATED = null;
				return;
			}

			final String sseFile = sseSelected + SUT_SETTINGS_EXT;

			try {
				File f = new File(settingsDir + File.separator + sseFile);
				if (f.createNewFile()) {
					SSE_ACTIVATED = sseSelected;
					return;
				}
			} catch (IOException e) {
				System.out.println("Exception creating <" + sseFile + "> file");
			}

		}
		SSE_ACTIVATED = null;
	}

	//TODO: After know what overrideWithUserProperties does, unify this method with loadSettings
	/**
	 * Load the settings of the selected test.settings file
	 * 
	 * @param args
	 * @param testSettingsFileName
	 * @return settings
	 */
	private static Settings loadTestarSettings(String[] args, String testSettingsFileName){

		Settings settings = null;
		try {
			settings = loadSettings(args, testSettingsFileName);
		} catch (ConfigException ce) {
			LogSerialiser.log("There is an issue with the configuration file: " + ce.getMessage() + "\n", LogSerialiser.LogLevel.Critical);
		}

		//TODO: Understand what this exactly does?
		overrideWithUserProperties(settings);
		Float SST = settings.get(ConfigTags.StateScreenshotSimilarityThreshold, null);
		if (SST != null) {
			System.setProperty("SCRSHOT_SIMILARITY_THRESHOLD", SST.toString());
		}

		return settings;
	}

	/**
	 * Open TESTAR GUI to allow the users modify the settings and the protocol with which the want run TESTAR
	 * 
	 * @param settings
	 * @param testSettingsFileName
	 * @return true if users starts TESTAR, or false is users close TESTAR
	 */
	public static boolean startTestarDialog(Settings settings, String testSettingsFileName) {
		// Start up the TESTAR Dialog
		try {
			if ((settings = new SettingsDialog().run(settings, testSettingsFileName)) == null) {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Start TESTAR protocol with the selected settings
	 * 
	 * This method get the specific protocol class of the selected settings to run TESTAR
	 * 
	 * @param settings
	 */
	private static void startTestar(Settings settings) {

		// Compile the Java protocols if AlwaysCompile setting is true
		if (settings.get(ConfigTags.AlwaysCompile)) {
			compileProtocol(Main.settingsDir, settings.get(ConfigTags.ProtocolClass));
		}

		URLClassLoader loader = null;

		try {
			List<String> cp = settings.get(MyClassPath);
			URL[] classPath = new URL[cp.size()];
			for (int i = 0; i < cp.size(); i++) {

				classPath[i] = new File(cp.get(i)).toURI().toURL();
			}

			loader = new URLClassLoader(classPath);

			String pc = settings.get(ProtocolClass);
			String protocolClass = pc.substring(pc.lastIndexOf('/')+1, pc.length());

			LogSerialiser.log("Trying to load TESTAR protocol in class '" +protocolClass +
					"' with class path '" + Util.toString(cp) + "'\n", LogSerialiser.LogLevel.Debug);

			@SuppressWarnings("unchecked")
			UnProc<Settings> protocol = (UnProc<Settings>) loader.loadClass(protocolClass).getConstructor().newInstance();
			LogSerialiser.log("TESTAR protocol loaded!\n", LogSerialiser.LogLevel.Debug);

			LogSerialiser.log("Starting TESTAR protocol ...\n", LogSerialiser.LogLevel.Debug);

			//Run TESTAR protocol with the selected settings
			protocol.run(settings);

		}catch (Throwable t) {
			LogSerialiser.log("An unexpected error occurred: " + t + "\n", LogSerialiser.LogLevel.Critical);
			System.out.println("Main: Exception caught");
			t.printStackTrace();
			t.printStackTrace(LogSerialiser.getLogStream());
		}
		finally {
			if (loader != null) {
				try {
					loader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			TestSerialiser.exit();
			ScreenshotSerialiser.exit();
			LogSerialiser.exit();
		}
	}

	// TODO: This methods should be part of the Settings class. It contains all the default values of the settings.
	/**
	 * Load the default settings for all the configurable settings and add/overwrite with those from the file
	 * This is needed because the user might not have set all the possible settings in the test.settings file.
	 * @param argv
	 * @param file
	 * @return An instance of Settings
	 * @throws ConfigException
	 */
	public static Settings loadSettings(String[] argv, String file) throws ConfigException {
		Assert.notNull(file);
		try {
			List<Pair<?, ?>> defaults = new ArrayList<Pair<?, ?>>();
			defaults.add(Pair.from(ProcessesToKillDuringTest, "(?!x)x"));
			defaults.add(Pair.from(ShowVisualSettingsDialogOnStartup, true));
			defaults.add(Pair.from(FaultThreshold, 0.1));
			defaults.add(Pair.from(LogLevel, 1));
			defaults.add(Pair.from(Mode, RuntimeControlsProtocol.Modes.Spy));
			defaults.add(Pair.from(OutputDir, outputDir));
			defaults.add(Pair.from(TempDir, tempDir));
			defaults.add(Pair.from(OnlySaveFaultySequences, false));
			defaults.add(Pair.from(PathToReplaySequence, tempDir));
			defaults.add(Pair.from(ActionDuration, 0.1));
			defaults.add(Pair.from(TimeToWaitAfterAction, 0.1));
			defaults.add(Pair.from(ExecuteActions, true));
			defaults.add(Pair.from(DrawWidgetUnderCursor, false));
			defaults.add(Pair.from(DrawWidgetInfo, true));
			defaults.add(Pair.from(VisualizeActions, false));
			defaults.add(Pair.from(VisualizeSelectedAction, false));
			defaults.add(Pair.from(SequenceLength, 10));
			defaults.add(Pair.from(ReplayRetryTime, 30.0));
			defaults.add(Pair.from(Sequences, 1));
			defaults.add(Pair.from(MaxTime, 31536000.0));
			defaults.add(Pair.from(StartupTime, 8.0));
			defaults.add(Pair.from(SUTConnectorValue, ""));
			defaults.add(Pair.from(Delete, new ArrayList<String>()));
			defaults.add(Pair.from(CopyFromTo, new ArrayList<Pair<String, String>>()));
			defaults.add(Pair.from(SuspiciousTitles, "(?!x)x"));
			defaults.add(Pair.from(ClickFilter, "(?!x)x"));
			defaults.add(Pair.from(MyClassPath, Arrays.asList(settingsDir)));
			defaults.add(Pair.from(ProtocolClass, "org.fruit.monkey.DefaultProtocol"));
			defaults.add(Pair.from(ForceForeground, true));
			defaults.add(Pair.from(UseRecordedActionDurationAndWaitTimeDuringReplay, true));
			defaults.add(Pair.from(StopGenerationOnFault, true));
			defaults.add(Pair.from(TimeToFreeze, 10.0));
			defaults.add(Pair.from(ShowSettingsAfterTest, true));
			defaults.add(Pair.from(RefreshSpyCanvas, 0.5));
			defaults.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
			defaults.add(Pair.from(TestGenerator, "random"));
			defaults.add(Pair.from(MaxReward, 9999999.0));
			defaults.add(Pair.from(Discount, .95));
			defaults.add(Pair.from(AlgorithmFormsFilling, false));
			defaults.add(Pair.from(TypingTextsForExecutedAction, 10));
			defaults.add(Pair.from(DrawWidgetTree, false));
			defaults.add(Pair.from(ExplorationSampleInterval, 1));
			defaults.add(Pair.from(GraphsActivated, true));
			defaults.add(Pair.from(PrologActivated, false));
			defaults.add(Pair.from(GraphResuming, true));
			defaults.add(Pair.from(ForceToSequenceLength, true));
			defaults.add(Pair.from(NonReactingUIThreshold, 100)); // number of executed actions
			defaults.add(Pair.from(OfflineGraphConversion, true));
			defaults.add(Pair.from(StateScreenshotSimilarityThreshold, Float.MIN_VALUE)); // disabled
			defaults.add(Pair.from(UnattendedTests, false)); // disabled
			defaults.add(Pair.from(AccessBridgeEnabled, false)); // disabled
			defaults.add(Pair.from(SUTProcesses, ""));
			defaults.add(Pair.from(StateModelEnabled, false));
			defaults.add(Pair.from(DataStore, ""));
			defaults.add(Pair.from(DataStoreType, ""));
			defaults.add(Pair.from(DataStoreServer, ""));
			defaults.add(Pair.from(DataStoreDirectory, ""));
			defaults.add(Pair.from(DataStoreDB, ""));
			defaults.add(Pair.from(DataStoreUser, ""));
			defaults.add(Pair.from(DataStorePassword, ""));
			defaults.add(Pair.from(DataStoreMode, ""));
			defaults.add(Pair.from(ResetDataStore, false));
			defaults.add(Pair.from(ApplicationName, ""));
			defaults.add(Pair.from(ApplicationVersion, ""));
			defaults.add(Pair.from(ActionSelectionAlgorithm, "random"));
			defaults.add(Pair.from(StateModelStoreWidgets, true));
			defaults.add(Pair.from(AlwaysCompile, true));
			defaults.add(Pair.from(ProcessListenerEnabled, false));
			defaults.add(Pair.from(SuspiciousProcessOutput, "(?!x)x"));
			defaults.add(Pair.from(ProcessLogs, ".*.*"));
			defaults.add(Pair.from(OverrideWebDriverDisplayScale, ""));
			defaults.add(Pair.from(ProtocolSpecificSetting_1, ""));
			defaults.add(Pair.from(ProtocolSpecificSetting_2, ""));
			defaults.add(Pair.from(ProtocolSpecificSetting_3, ""));
			defaults.add(Pair.from(ProtocolSpecificSetting_4, ""));
			defaults.add(Pair.from(ProtocolSpecificSetting_5, ""));

			defaults.add(Pair.from(AbstractStateAttributes, new ArrayList<String>() {
				{
					add("WidgetControlType");
				}
			}));

			defaults.add(Pair.from(ClickableClasses, new ArrayList<String>() {
				{
					add("v-menubar-menuitem");
					add("v-menubar-menuitem-caption");
				}
			}));

			defaults.add(Pair.from(DeniedExtensions, new ArrayList<String>() {
				{
					add("pdf");
					add("jpg");
					add("png");
				}
			}));

			defaults.add(Pair.from(DomainsAllowed, new ArrayList<String>() {
				{
					add("www.ou.nl");
					add("mijn.awo.ou.nl");
					add("login.awo.ou.nl");
				}
			}));

            defaults.add(Pair.from(TagsToFilter, new ArrayList<String>() {
                {
                    add("Title");
                    add("WebName");
                    add("WebTagName");
                }
            }));


			defaults.add(Pair.from(TagsForSuspiciousOracle, new ArrayList<String>() {
				{
					add("Title");
					add("WebName");
					add("WebTagName");
				}
			}));

			defaults.add(Pair.from(FollowLinks, true));
			defaults.add(Pair.from(BrowserFullScreen, true));
			defaults.add(Pair.from(SwitchNewTabs, true));

			/*
			//TODO web driver settings for login feature
			defaults.add(Pair.from(Login, null)); // null = feature not enabled
			// login = Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
			defaults.add(Pair.from(Username, ""));
			defaults.add(Pair.from(Password, ""));
			*/

			//Overwrite the default settings with those from the file
			Settings settings = Settings.fromFile(defaults, file);

			//If user use command line to input properties, mix file settings with cmd properties
			if(argv.length>0) {
				try {
					settings = Settings.fromFileCmd(defaults, file, argv);
				}catch(Exception e) {
					System.out.println("Error with command line properties. Examples:");
					System.out.println("testar SUTConnectorValue=\"C:\\\\Windows\\\\System32\\\\notepad.exe\" Sequences=11 SequenceLength=12 SuspiciousTitle=.*aaa.*");
					System.out.println("SUTConnectorValue=\" \"\"C:\\\\Program Files\\\\Internet Explorer\\\\iexplore.exe\"\" \"\"https://www.google.es\"\" \"");
				}
				//SUTConnectorValue=" ""C:\\Program Files\\Internet Explorer\\iexplore.exe"" ""https://www.google.es"" "
				//SUTConnectorValue="C:\\Windows\\System32\\notepad.exe"
			}

			//Make sure that Prolog is ALWAYS false, even if someone puts it to true in their test.settings file
			//Need this during refactoring process of getting Prolog code out. Refactoring will assume that
			//PrologActivated is ALWAYS false.
			//Evidently it will now be IMPOSSIBLE for it to be true hahahahahahaha
			settings.set(ConfigTags.PrologActivated, false);

			// check that the abstract state properties and the abstract action properties have at least 1 value
			if ((settings.get(AbstractStateAttributes)).isEmpty()) {
				throw new ConfigException("Please provide at least 1 valid abstract state attribute or leave the key out of the settings file");
			}

			try{
				settings.get(ConfigTags.ExtendedSettingsFile);
			} catch (NoSuchTagException e){
				settings.set(ConfigTags.ExtendedSettingsFile, file.replace(SETTINGS_FILE, ExtendedSettingFile.FileName));
			}
			ExtendedSettingsFactory.Initialize(settings.get(ConfigTags.ExtendedSettingsFile));

			return settings;
		} catch (IOException ioe) {
			throw new ConfigException("Unable to load configuration file!", ioe);
		}
	}

	/**
	 * This method creates a sse file to change TESTAR protocol if sett param matches an existing protocol
	 * @param sett
	 * @throws IOException 
	 */
	public static void protocolFromCmd(String sett) throws IOException {
		String sseName = sett.substring(sett.indexOf("=")+1);
		boolean existSSE = false;

		//Check if choose protocol exist
		for (File f : new File(settingsDir).listFiles()) {
			if (new File(settingsDir + sseName + File.separator + SETTINGS_FILE).exists()) {
				existSSE = true;
				break;
			}
		}

		//Command line protocol doesn't exist
		if(!existSSE) {System.out.println("Protocol: "+sseName+" doesn't exist");}

		else{
			//Obtain previous sse file and delete it (if exist)
			String[] files = getSSE();
			if (files != null) {
				for (String f : files) 
					new File(settingsDir+f).delete();

			}

			//Create the new sse file
			String sseDir = settingsDir + sseName + SUT_SETTINGS_EXT;
			File f = new File(sseDir);
			if(!f.exists())
				f.createNewFile();

			System.out.println("Protocol changed from command line to: "+sseName);

		}
	}

	//TODO: Understand what this exactly does?
	/**
	 * Override something. Not sure what
	 * @param settings
	 */
	private static void overrideWithUserProperties(Settings settings) {
		String pS, p;
		// headless mode
		pS = ConfigTags.ShowVisualSettingsDialogOnStartup.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("headless", null); // mnemonic
		}
		if (p != null) {
			settings.set(ConfigTags.ShowVisualSettingsDialogOnStartup, !(new Boolean(p).booleanValue()));
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
		}
		// TestGenerator
		pS = ConfigTags.TestGenerator.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("TG", null); // mnemonic
		}
		if (p != null) {
			settings.set(ConfigTags.TestGenerator, p);
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
		}
		// SequenceLength
		pS = ConfigTags.SequenceLength.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("SL", null); // mnemonic
		}
		if (p != null) {
			try {
				Integer sl = new Integer(p);
				settings.set(ConfigTags.SequenceLength, sl);
				LogSerialiser.log("Property <" + pS + "> overridden to <" + sl.toString() + ">", LogSerialiser.LogLevel.Critical);
			} catch (NumberFormatException e) {
				LogSerialiser.log("Property <" + pS + "> could not be set! (using default)", LogSerialiser.LogLevel.Critical);
			}
		}
		// GraphResumingActivated
		pS = ConfigTags.GraphResuming.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("GRA", null); // mnemonic
		}
		if (p != null) {
			settings.set(ConfigTags.GraphResuming, new Boolean(p).booleanValue());
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
		}
		// ForceToSequenceLength
		pS = ConfigTags.ForceToSequenceLength.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("F2SL", null); // mnemonic
		}
		if (p != null) {
			settings.set(ConfigTags.ForceToSequenceLength, new Boolean(p).booleanValue());
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
		}
		// TypingTextsForExecutedAction
		pS = ConfigTags.TypingTextsForExecutedAction.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("TT", null); // mnemonic
		}
		if (p != null) {
			try {
				Integer tt = new Integer(p);
				settings.set(ConfigTags.TypingTextsForExecutedAction, tt);
				LogSerialiser.log("Property <" + pS + "> overridden to <" + tt.toString() + ">", LogSerialiser.LogLevel.Critical);
			} catch (NumberFormatException e) {
				LogSerialiser.log("Property <" + pS + "> could not be set! (using default)", LogSerialiser.LogLevel.Critical);
			}
		}
		// StateScreenshotSimilarityThreshold
		pS = ConfigTags.StateScreenshotSimilarityThreshold.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("SST", null); // mnemonic
		}
		if (p != null) {
			try {
				Float sst = new Float(p);
				settings.set(ConfigTags.StateScreenshotSimilarityThreshold, sst);
				LogSerialiser.log("Property <" + pS + "> overridden to <" + sst.toString() + ">", LogSerialiser.LogLevel.Critical);
			} catch (NumberFormatException e) {
				LogSerialiser.log("Property <" + pS + "> could not be set! (using default)", LogSerialiser.LogLevel.Critical);
			}
		}
		// UnattendedTests
		pS = ConfigTags.UnattendedTests.name();
		p = System.getProperty(pS, null);
		if (p == null) {
			p = System.getProperty("UT", null); // mnemonic
		}
		if (p != null) {
			settings.set(ConfigTags.UnattendedTests, new Boolean(p).booleanValue());
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
		}
	}

	/**
	 * This method initializes the coding manager with custom tags to use for constructing
	 * concrete and abstract state ids, if provided of course.
	 * @param settings
	 */
	private static void initCodingManager(Settings settings) {
		// we look if there are user-provided custom state tags in the settings
		// if so, we provide these to the coding manager

        Set<Tag<?>> stateManagementTags = StateManagementTags.getAllTags();
        // for the concrete state tags we use all the state management tags that are available
		if (!stateManagementTags.isEmpty()) {
			CodingManager.setCustomTagsForConcreteId(stateManagementTags.toArray(new Tag<?>[0]));
		}

        // then the attributes for the abstract state id
        if (!settings.get(ConfigTags.AbstractStateAttributes).isEmpty()) {
            Tag<?>[] abstractTags = settings.get(AbstractStateAttributes).stream().map(StateManagementTags::getTagFromSettingsString).filter(Objects::nonNull).toArray(Tag<?>[]::new);
            CodingManager.setCustomTagsForAbstractId(abstractTags);
        }
    }

	/**
	 * Set the concrete implementation of IEnvironment based on the Operating system on which the application is running.
	 */
	private static void initOperatingSystem() {
		if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WINDOWS_10)) {
			Environment.setInstance(new Windows10());
		} else {
			System.out.printf("WARNING: Current OS %s has no concrete environment implementation, using default environment\n", NativeLinker.getPLATFORM_OS());
			Environment.setInstance(new UnknownEnvironment());
		}
	}
}
