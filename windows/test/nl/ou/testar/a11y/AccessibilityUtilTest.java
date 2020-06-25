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

package nl.ou.testar.a11y;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.ou.testar.a11y.windows.AccessibilityUtil;

public class AccessibilityUtilTest {

	@Test
	public void testParseShortcutKey() {
		String exp;
		
		// single keys
		exp =
				"(70)" // press
				+ "(70)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_F").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("  VK_F  ").toParametersString());
		exp =
				"(27)" // press
				+ "(27)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_ESCAPE").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("vk_escape").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_ESCAPE").toParametersString());
		exp =
				"(17)" // press
				+ "(17)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_CONTROL").toParametersString());
		
		// key combinations
		exp =
				"(17)(16)(70)" // press
				+ "(70)(16)(17)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_CONTROL+VK_SHIFT+VK_F").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("  VK_CONTROL+VK_SHIFT+VK_F  ").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_CONTROL+VK_SHIFT+VK_F").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_CONTROL+VK_SHIFT+VK_F").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_CONTROL+VK_SHIFT+VK_F").toParametersString());
		
		// keys that have different symbols when Shift is pressed
		exp =
				"(17)(16)(91)" // press
				+ "(91)(16)(17)"; // release
		// the Action should send the non-Shift'ed key
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_CONTROL+VK_SHIFT+VK_OPEN_BRACKET").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("VK_CONTROL+VK_SHIFT+VK_OPEN_BRACKET").toParametersString());
		
		// invalid strings
		assertNull(AccessibilityUtil.parseShortcutKey("  "));
	}

}
