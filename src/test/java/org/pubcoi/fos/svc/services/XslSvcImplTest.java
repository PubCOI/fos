/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XslSvcImplTest {

    XslSvcImpl svc;

    @Before
    public void setup() {
        svc = new XslSvcImpl();
    }

    @Test
    public void testEntityCleanupDoesNotRemoveValidEntries() {
        String testStr01 = "<test>item &amp; abc</test>";
        Assert.assertEquals(testStr01, svc.removeInvalidEntities(testStr01));
        String testStr02 = "<test>item &lt;abc</test>";
        Assert.assertEquals(testStr02, svc.removeInvalidEntities(testStr02));
        String testStr03 = "<test input=\"abc-&amp;\">item abc</test>";
        Assert.assertEquals(testStr03, svc.removeInvalidEntities(testStr03));
    }

    @Test
    public void testEntityCleanupReplacesKnownEntities() {
        Assert.assertEquals("<test>item £100</test>", svc.removeInvalidEntities("<test>item &pound;100</test>"));
        Assert.assertEquals("<test>item •100</test>", svc.removeInvalidEntities("<test>item &bull;100</test>"));
        Assert.assertEquals("<test>item •••</test>", svc.removeInvalidEntities("<test>item &bull;&bull;&bull;</test>"));
    }

    @Test
    public void testEntityCleanupRemovesUnknownEntities() {
        Assert.assertEquals("<test>item abc 123</test>", svc.removeInvalidEntities("<test>item &null;abc 123</test>"));
        Assert.assertEquals("<test>item abc 123</test>", svc.removeInvalidEntities("<test>item &null;abc &Testing;123</test>"));
        Assert.assertEquals("<test>item abc 123</test>", svc.removeInvalidEntities("<test>item &null;abc &29abc;123</test>"));
    }

}