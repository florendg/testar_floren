/***************************************************************************************************
 *
 * Copyright (c) 2017 Open Universiteit - www.ou.nl
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


package nl.ou.testar.a11y.windows;

import es.upv.staq.testar.serialisation.LogSerialiser;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.windows.UIATags;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.fruit.alayer.windows.UIARoles.UIAComboBox;
import static org.fruit.alayer.windows.UIARoles.UIADocument;
import static org.fruit.alayer.windows.UIARoles.UIAEdit;
import static org.fruit.alayer.windows.UIARoles.UIAImage;
import static org.fruit.alayer.windows.UIARoles.UIAListItem;
import static org.fruit.alayer.windows.UIARoles.UIAMenu;
import static org.fruit.alayer.windows.UIARoles.UIAMenuItem;
import static org.fruit.alayer.windows.UIARoles.UIARadioButton;
import static org.fruit.alayer.windows.UIARoles.UIASlider;
import static org.fruit.alayer.windows.UIARoles.UIASpinner;
import static org.fruit.alayer.windows.UIARoles.UIATabItem;
import static org.fruit.alayer.windows.UIARoles.UIAText;
import static org.fruit.alayer.windows.UIARoles.UIATreeItem;
import static org.fruit.alayer.windows.UIARoles.UIAUnknown;
import static org.fruit.alayer.windows.UIARoles.UIAWidget;
import static org.fruit.alayer.windows.UIARoles.UIAWindow;

/**
 * Various utilities for evaluating accessibility
 *
 * @author Davy Kager
 */
public final class AccessibilityUtil {

    private static final String KEY_SEP_REGEX = "[+]{1}";
    private static final String VIRTUAL_KEY_PREFIX = "VK_";
    private static final StdActionCompiler compiler = new StdActionCompiler();

    private static final List<Action> AC_LEFT_RIGHT = Arrays.asList(
            parseShortcutKey("VK_LEFT"), parseShortcutKey("VK_RIGHT"));
    private static final Role[] R_LEFT_RIGHT = new Role[]{
            UIAMenuItem, UIATabItem, UIATreeItem,
            UIADocument, UIAEdit,
            UIARadioButton, UIASlider
    };

    private static final List<Action> AC_UP_DOWN = Arrays.asList(
            parseShortcutKey("VK_UP"), parseShortcutKey("VK_DOWN"));
    private static final Role[] R_UP_DOWN = new Role[]{
            UIAListItem, UIAMenu, UIAMenuItem, UIATreeItem,
            UIADocument, UIAEdit,
            UIAComboBox, UIARadioButton, UIASlider, UIASpinner
    };

    private static final List<Action> AC_HOME_END = Arrays.asList(
            parseShortcutKey("VK_HOME"), parseShortcutKey("VK_END"),
            parseShortcutKey("VK_CONTROL+VK_HOME"), parseShortcutKey("VK_CONTROL+VK_END"));
    private static final Role[] R_HOME_END = new Role[]{
            UIADocument, UIAEdit
    };

    private static final List<Action> AC_DELETE = Arrays.asList(
            parseShortcutKey("VK_DELETE"), parseShortcutKey("VK_BACK_SPACE"));
    private static final Role[] R_DELETE = new Role[]{
            UIADocument, UIAEdit, UIAText
    };

    /**
     * Standard navigational actions
     * These will work in most areas of a GUI.
     */
    public static final Action
            AC_ACTIVATE_WIDGET = parseShortcutKey("VK_ENTER"),
            AC_CANCEL = parseShortcutKey("VK_ESCAPE"),
            AC_OPEN_CONTEXT_MENU = parseShortcutKey("VK_CONTEXT_MENU"),
            AC_NAVIGATE_NEXT_WIDGET = parseShortcutKey("VK_TAB"),
            AC_NAVIGATE_PREVIOUS_WIDGET = parseShortcutKey("VK_SHIFT+VK_TAB"),
            AC_NAVIGATE_NEXT_GUI_AREA = parseShortcutKey("VK_F6"),
            AC_NAVIGATE_PREVIOUS_GUI_AREA = parseShortcutKey("VK_SHIFT+VK_F6"),
            AC_NAVIGATE_NEXT_TAB = parseShortcutKey("VK_CONTROL+VK_TAB"),
            AC_NAVIGATE_PREVIOUS_TAB = parseShortcutKey("VK_CONTROL+VK_SHIFT+VK_TAB");

    /**
     * The prefix for log messages related to accessibility
     */
    public static final String LOG_PREFIX = "[a11y]";


    private AccessibilityUtil() {
    }

    /**
     * Compiles a compound action for the given shortcut key combination
     * A shortcut key of the form "Ctrl+V" is transformed into the key actions for the Control key and the 'v' key.
     * The method returns null if the key combination could not be parsed.
     * Compiles a compound action for the given shortcut key combination
     * A shortcut key of the form "Ctrl+V" is transformed into the key actions for the Control key and the 'v' key.
     * The method returns null if the key combination could not be parsed.
     *
     * @param combination The shortcut key.
     * @return The compound action, or null if the combination could not be parsed.
     */
    public static Action parseShortcutKey(String combination) {
        Assert.hasText(combination);
        String[] keyStrings = combination.trim().split(KEY_SEP_REGEX);
        if (keyStrings.length == 0) {
            logA11y("Failed to parse shortcut key <" + combination + ">");
            return null;
        }
        List<Integer> keys = new ArrayList<>();
        for (String keyString : keyStrings) {
            if (keyString.isEmpty()) {
                logA11y("Failed to parse part of shortcut key <" + combination + ">");
                return null;
            }
            keyString = keyString.toUpperCase();
            if (AlternativeKeyNames.map.containsKey(keyString))
                keyString = AlternativeKeyNames.map.get(keyString);
            Field vkString;
            try {
                vkString = KeyEvent.class.getField(keyString);
                keys.add((int) vkString.get(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return compiler.hitShortcutKey(keys);
    }

    // ####################
    // UIA querying methods
    // ####################

    // The methods below can be moved to Tags on the Widget and/or to NativeLinker.
    // This has not yet been done because it requires finding appropriate equivalents
    // for the UIA properties in other accessibility APIs.

    /**
     * Checks whether the given widget is relevant to accessibility evaluation
     * Checks whether the given widget is relevant to accessibility evaluation
     *
     * @param w The widget.
     * @return True if the widget is relevant, else false.
     */
    public static boolean isRelevant(Widget w) {
        // NOTE: UIA doesn't accurately report the isKeyboardFocusable property,
        // so don't use it when filtering out irrelevant widgets.
        return !(w instanceof State) // filter out the root of the widget tree
                && w.get(UIATags.UIAIsContentElement, true)
                && w.get(UIATags.UIAIsEnabled, true);
    }

    /**
     * Gets all non-standard actions that can be performed on the given widget
     * Gets all non-standard actions that can be performed on the given widget
     *
     * @param w The widget.
     * @return The set of actions.
     */
    public static Set<Action> getApplicableActions(Widget w) {
        Role r = getRole(w);
        Set<Action> actions = new HashSet<>();
        if (Role.isOneOf(r, R_LEFT_RIGHT))
            actions.addAll(AC_LEFT_RIGHT);
        if (Role.isOneOf(r, R_UP_DOWN))
            actions.addAll(AC_UP_DOWN);
        if (Role.isOneOf(r, R_HOME_END))
            actions.addAll(AC_HOME_END);
        if (Role.isOneOf(r, R_DELETE))
            actions.addAll(AC_DELETE);
        return actions;
    }

    /**
     * Gets the language for the given widget
     * Gets the language for the given widget
     *
     * @param w The widget.
     * @return The language, as unprocessed language code.
     */
    public static long getLanguage(Widget w) {
        return w.get(UIATags.UIACulture, 0L);
    }

    /**
     * Checks whether the given widget has keyboard focus
     * Checks whether the given widget has keyboard focus
     *
     * @param w The widget.
     * @return True if the widget has keyboard focus, else false.
     */
    public static boolean hasKeyboardFocus(Widget w) {
        return w.get(UIATags.UIAHasKeyboardFocus, false);
    }

    /**
     * Checks whether the given widget can receive keyboard focus
     *
     * @param w The widget.
     * @return True if the widget can receive keyboard focus, else false.
     */
    public static boolean isKeyboardFocusable(Widget w) {
        return w.get(UIATags.UIAIsKeyboardFocusable, false);
    }

    /**
     * Gets the access key for the given widget
     *
     * @param w The widget.
     * @return The access key, as unprocessed string.
     */
    public static String getAccessKey(Widget w) {
        return w.get(UIATags.UIAAccessKey, "");
    }

    /**
     * Gets the accelerator key for the given widget
     *
     * @param w The widget.
     * @return The shortcut key, as unprocessed string.
     */
    public static String getAcceleratorKey(Widget w) {
        String key = w.get(UIATags.UIAAcceleratorKey, "");
        if (key != null && !key.isEmpty())
            return key;
        if (getRole(w).isA(UIAMenuItem)) {
            // many menu items contain an accelerator key even if the accelerator key property is not set
            // find these by pattern matching (may return the wrong thing, but better than nothing)
            String name = w.get(UIATags.UIAName, "");
            int index = name.lastIndexOf("\t");
            if (index != -1) {
                key = name.substring(index + 1);
                logA11y("Discovered shortcut key <" + key + "> in <" + name + ">");
                return key;
            }
        }
        return "";
    }

    /**
     * Checks whether shortcut keys can be used provided that the given widget has keyboard focus
     *
     * @param focusW The widget with keyboard focus.
     * @return True if shortcut keys can be used, else false.
     */
    public static boolean canUseShortcutKeys(Widget focusW) {
        Assert.isTrue(hasKeyboardFocus(focusW),
                "Only applies to the widget with keyboard focus");
        return !focusW.root().get(UIATags.UIAIsWindowModal, false)
                && !Role.isOneOf(getRole(focusW), new Role[]{UIAMenu, UIAMenuItem});
    }

    /**
     * Checks whether the role of the given widget is unknown
     *
     * @param w The widget.
     * @return True if the widget role is unknown, else false.
     */
    public static boolean isRoleUnknown(Widget w) {
        return Role.isOneOf(getRole(w), UIAUnknown);
    }

    /**
     * Checks whether the given widget is an image
     *
     * @param w The widget.
     * @return True if the widget is an image, else false.
     */
    public static boolean isImage(Widget w) {
        return getRole(w).isA(UIAImage);
    }

    /**
     * Checks whether the given widget is a tab item
     *
     * @param w The widget.
     * @return True if the widget is a tab item, else false.
     */
    public static boolean isTabItem(Widget w) {
        return getRole(w).isA(UIATabItem);
    }

    /**
     * Checks whether the given widget is a window
     *
     * @param w The widget.
     * @return True if the widget is a window, else false.
     */
    public static boolean isWindow(Widget w) {
        return getRole(w).isA(UIAWindow);
    }

    // ##############
    // helper methods
    // ##############

    private static Role getRole(Widget w) {
        Assert.notNull(w);
        return w.get(Tags.Role, UIAWidget);
    }

    // #########
    // debugging
    // #########

    /**
     * Prints debug info about a widget that is useful for evaluating accessibility
     *
     * @param w The widget.
     */
    public static void printWidgetDebugInfo(Widget w) {
        logA11y("Widget" + (w.get(UIATags.UIAHasKeyboardFocus, false) ? " [focus]" : "")
                        + ": <" + w.get(Tags.Title, "unknown widget")
                        + ">@" + w.get(Tags.ZIndex) + "/" + w.root().get(Tags.MaxZIndex)
                        + " (" + w.get(Tags.Role, Roles.Invalid) + ")"
                        + ": <" + w.get(UIATags.UIAAccessKey, "no access key")
                        + "> - <" + w.get(UIATags.UIAAcceleratorKey, "no accelerator key") + ">",
                LogSerialiser.LogLevel.Debug);
    }

    /**
     * Logs a message about accessibility
     * An identifying prefix <code>LOG_PREFIX)</code> is added to the message,
     * and a newline character is appended.
     *
     * @param msg The message to log.
     */
    public static void logA11y(String msg) {
        logA11y(msg, LogSerialiser.LogLevel.Info);
    }

    /**
     * Logs a message about accessibility
     * An identifying prefix <code>LOG_PREFIX)</code> is added to the message,
     * and a newline character is appended.
     *
     * @param msg      The message to log.
     * @param logLevel The log level.
     */
    public static void logA11y(String msg, LogSerialiser.LogLevel logLevel) {
        LogSerialiser.log(LOG_PREFIX + " " + msg + "\n", logLevel);
    }

}
