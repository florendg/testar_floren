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


package org.fruit.monkey;

import org.fruit.Pair;
import org.fruit.alayer.Tag;

import java.util.List;

public final class ConfigTags {
  public static final Tag<String> ProcessesToKillDuringTest = Tag.from("ProcessesToKillDuringTest", String.class);
  public static final Tag<Boolean> ShowVisualSettingsDialogOnStartup = Tag.from("ShowVisualSettingsDialogOnStartup", Boolean.class);
  public static final Tag<Integer> LogLevel = Tag.from("LogLevel", Integer.class);
  public static final Tag<String> SuspiciousTitles = Tag.from("SuspiciousTitles", String.class);
  public static final Tag<String> ClickFilter = Tag.from("ClickFilter", String.class);
  public static final Tag<String> OutputDir = Tag.from("OutputDir", String.class);
  public static final Tag<String> TempDir = Tag.from("TempDir", String.class);
  public static final Tag<Boolean> OnlySaveFaultySequences = Tag.from("OnlySaveFaultySequences", Boolean.class);
  public static final Tag<Boolean> ForceForeground = Tag.from("ForceForeground", Boolean.class);
  public static final Tag<Double> ActionDuration = Tag.from("ActionDuration", Double.class);
  public static final Tag<Double> FaultThreshold = Tag.from("FaultThreshold", Double.class);
  public static final Tag<Double> TimeToWaitAfterAction = Tag.from("TimeToWaitAfterAction", Double.class);
  public static final Tag<Boolean> VisualizeActions = Tag.from("VisualizeActions", Boolean.class);
  public static final Tag<Boolean> VisualizeSelectedAction = Tag.from("VisualizeSelectedAction", Boolean.class);
  public static final Tag<Boolean> DrawWidgetUnderCursor = Tag.from("DrawWidgetUnderCursor", Boolean.class);
  public static final Tag<Boolean> DrawWidgetInfo = Tag.from("DrawWidgetInfo", Boolean.class);
  public static final Tag<Boolean> ExecuteActions = Tag.from("ExecuteActions", Boolean.class);
  public static final Tag<String> PathToReplaySequence = Tag.from("PathToReplaySequence", String.class);
  public static final Tag<RuntimeControlsProtocol.Modes> Mode = Tag.from("Mode", RuntimeControlsProtocol.Modes.class);
  public static final Tag<String> SUTConnectorValue = Tag.from("SUTConnectorValue", String.class);
  public static final Tag<Integer> SequenceLength = Tag.from("SequenceLength", Integer.class);
  public static final Tag<Integer> Sequences = Tag.from("Sequences", Integer.class);
  public static final Tag<Double> ReplayRetryTime = Tag.from("ReplayRetryTime", Double.class);
  public static final Tag<Double> MaxTime = Tag.from("MaxTime", Double.class);
  public static final Tag<Double> StartupTime = Tag.from("StartupTime", Double.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> Delete = Tag.from("Delete", (Class<List<String>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<Pair<String, String>>> CopyFromTo = Tag.from("CopyFromTo", (Class<List<Pair<String, String>>>) (Class<?>) List.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> MyClassPath = Tag.from("MyClassPath", (Class<List<String>>) (Class<?>) List.class);
  public static final Tag<String> ProtocolClass = Tag.from("ProtocolClass", String.class);
  public static final Tag<Boolean> UseRecordedActionDurationAndWaitTimeDuringReplay = Tag.from("UseRecordedActionDurationAndWaitTimeDuringReplay", Boolean.class);
  public static final Tag<Boolean> StopGenerationOnFault = Tag.from("StopGenerationOnFault", Boolean.class);
  public static final Tag<Double> TimeToFreeze = Tag.from("TimeToFreeze", Double.class);
  public static final Tag<Boolean> ShowSettingsAfterTest = Tag.from("ShowSettingsAfterTest", Boolean.class);

  public static final Tag<String> SUTConnector = Tag.from("SUTConnector", String.class);
  public static final Tag<String> TestGenerator = Tag.from("TestGenerator", String.class);
  public static final Tag<Double> MaxReward = Tag.from("MaxReward", Double.class);
  public static final Tag<Double> Discount = Tag.from("Discount", Double.class);
  public static final Tag<Boolean> AlgorithmFormsFilling = Tag.from("AlgorithmFormsFilling", Boolean.class);
  public static final Tag<Integer> TypingTextsForExecutedAction = Tag.from("TypingTextsForExecutedAction", Integer.class);
  public static final Tag<Boolean> DrawWidgetTree = Tag.from("DrawWidgetTree", Boolean.class);
  public static final Tag<Integer> ExplorationSampleInterval = Tag.from("ExplorationSampleInterval", Integer.class);
  public static final Tag<Boolean> GraphsActivated = Tag.from("GraphsActivated", Boolean.class);
  public static final Tag<Boolean> PrologActivated = Tag.from("PrologActivated", Boolean.class);
  public static final Tag<Boolean> GraphResuming = Tag.from("GraphResuming", Boolean.class);
  public static final Tag<Boolean> ForceToSequenceLength = Tag.from("ForceToSequenceLength", Boolean.class);
  public static final Tag<Integer> NonReactingUIThreshold = Tag.from("NonReactingUIThreshold", Integer.class);
  public static final Tag<Boolean> OfflineGraphConversion = Tag.from("OfflineGraphConversion", Boolean.class);
  public static final Tag<Float> StateScreenshotSimilarityThreshold = Tag.from("StateScreenshotSimilarityThreshold", Float.class);
  public static final Tag<Boolean> UnattendedTests = Tag.from("UnattendedTests", Boolean.class);
  public static final Tag<Boolean> AccessBridgeEnabled = Tag.from("AccessBridgeEnabled", Boolean.class);
  public static final Tag<String> SUTProcesses = Tag.from("SUTProcesses", String.class); // Shift+0 shortcut to debug (STDOUT) windows' process names

  // graph db config tags
  public static final Tag<Boolean> GraphDBEnabled = Tag.from("GraphDBEnabled", Boolean.class);
  public static final Tag<String> GraphDBUrl = Tag.from("GraphDBUrl", String.class);
  public static final Tag<String> GraphDBUser = Tag.from("GraphDBUser", String.class);
  public static final Tag<String> GraphDBPassword = Tag.from("GraphDBPassword", String.class);

  // state model config tags
  public static final Tag<Boolean> StateModelEnabled = Tag.from("StateModelEnabled", Boolean.class);
  public static final Tag<String> DataStore = Tag.from("DataStore", String.class);
  public static final Tag<String> DataStoreType = Tag.from("DataStoreType", String.class);
  public static final Tag<String> DataStoreServer = Tag.from("DataStoreServer", String.class);
  public static final Tag<String> DataStoreDB = Tag.from("DataStoreDB", String.class);
  public static final Tag<String> DataStoreUser = Tag.from("DataStoreUser", String.class);
  public static final Tag<String> DataStorePassword = Tag.from("DataStorePassword", String.class);
  public static final Tag<String> DataStoreMode = Tag.from("DataStoreMode", String.class);
  public static final Tag<String> DataStoreDirectory = Tag.from("DataStoreDirectory", String.class);
  public static final Tag<Boolean> ResetDataStore = Tag.from("ResetDataStore", Boolean.class);
  public static final Tag<String> ApplicationName = Tag.from("ApplicationName", String.class);
  public static final Tag<String> ApplicationVersion = Tag.from("ApplicationVersion", String.class);
  public static final Tag<String> ActionSelectionAlgorithm = Tag.from("ActionSelectionAlgorithm", String.class);
  public static final Tag<Boolean> StateModelStoreWidgets = Tag.from("StateModelStoreWidgets", Boolean.class);
  @SuppressWarnings("unchecked")
  public static final Tag<List<String>> AbstractStateAttributes = Tag.from("AbstractStateAttributes", (Class<List<String>>) (Class<?>) List.class);

  public static final Tag<Boolean> AlwaysCompile = Tag.from("AlwaysCompile", Boolean.class);

  public static final Tag<Boolean> ProcessListenerEnabled = Tag.from("ProcessListenerEnabled", Boolean.class);
  public static final Tag<String> SuspiciousProcessOutput = Tag.from("SuspiciousProcessOutput", String.class);
  public static final Tag<String> ProcessLogs = Tag.from("ProcessLogs", String.class);
  
  public static final Tag<String> InputFileText = Tag.from("InputFileText", String.class);

  // Note: Defined the tag as string on purpose so we can leave the default value empty in the pre defined settings.
  public static final Tag<String> OverrideWebDriverDisplayScale = Tag.from("OverrideWebDriverDisplayScale", String.class);

}
