/**
 *  @(#)WikiTests.java
 *  Copyright (C) 2011 MER-C
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package test;

import java.io.*;
import java.util.*;
import java.util.logging.LogManager;
import org.wikipedia.Wiki;

/**
 *  Tests for Wiki.java that do not require being logged in.
 *  @author MER-C
 */
public class WikiTests
{
    public static void main(String[] args) throws IOException
    {
        // setup stuff
        System.setProperty("wiki.level", "100");
        LogManager.getLogManager().readConfiguration();
        Wiki enWiki = new Wiki("en.wikipedia.org");
        enWiki.setMaxLag(-1);
/*           
        // random
        System.out.println(enWiki.random());
        System.out.println(enWiki.random());
        System.out.println(enWiki.random());
        System.out.println(enWiki.random(Wiki.PROJECT_NAMESPACE, Wiki.USER_NAMESPACE));
        System.out.println(enWiki.random(Wiki.PROJECT_NAMESPACE, Wiki.USER_NAMESPACE));
        System.out.println(enWiki.random(Wiki.PROJECT_NAMESPACE, Wiki.USER_NAMESPACE));
        
    
        // imageUsage()
        for (String page : enWiki.imageUsage("Wiki.png", Wiki.PROJECT_NAMESPACE, Wiki.TEMPLATE_NAMESPACE))
            System.out.println(page);

        // getCategoryMembers()
        for (String page : enWiki.getCategoryMembers("Place of death missing"))
            System.out.println(page);
        for (String page : enWiki.getCategoryMembers("Miscellaneous pages for deletion", Wiki.USER_NAMESPACE, Wiki.TEMPLATE_NAMESPACE))
            System.out.println(page);

        // getPageHistory()
        for(Wiki.Revision rev : enWiki.getPageHistory("User_talk:MER-C"))
            System.out.println(rev);
        for(Wiki.Revision rev : enWiki.getPageHistory("A. K. Fazlul Huq"))
            System.out.println(rev);

        // getPageInfo(): protected, cascade protected page
        HashMap<String, Object> blah = enWiki.getPageInfo("Main Page");
        for(Map.Entry<String, Object> entry : blah.entrySet())
        {
            System.out.print(entry.getKey());
            System.out.print(" => ");
            System.out.println(entry.getValue());
        }
        System.out.println();
        // getPageInfo(): protected, deleted page
        blah = enWiki.getPageInfo("Create a new page");
        for(Map.Entry<String, Object> entry : blah.entrySet())
        {
            System.out.print(entry.getKey());
            System.out.print(" => ");
            System.out.println(entry.getValue());
        }

        // getInterWikiBacklinks()
        String[][] blah2 = enWiki.getInterWikiBacklinks("de", "");
        for (String[] entry : blah2)
            System.out.println(entry[0] + " => " + entry[1]);

        // getLinksOnPage
        for (String link : enWiki.getLinksOnPage("List of craters on Venus"))
            System.out.println(link);

        // getImagesOnPage
        for (String image : enWiki.getImagesOnPage("Main Page"))
            System.out.println(image);
        // This site runs an obsolete MW where "File:" doesn't exist
        for (String image : new Wiki("wiki.eclipse.org", "").getImagesOnPage("Main Page"))
            System.out.println(image);

        // getImage()
        byte[] image = enWiki.getImage("Wiki.png");
        FileOutputStream out = new FileOutputStream("Wiki.png");
        out.write(image);
        out.close();

        // User.getUserInfo()
        Wiki.User user = enWiki.getUser("Jimbo Wales");
        for(Map.Entry<String, Object> entry : user.getUserInfo().entrySet())
        {
            System.out.print(entry.getKey());
            System.out.print(" => ");
            Object temp = entry.getValue();
            System.out.println(temp instanceof Object[] ? Arrays.toString((Object[])temp) : temp);
        }
        System.out.println();

        //getLogEntries() - user rights log
        for(Wiki.LogEntry entry : enWiki.getLogEntries(null, null, 5, Wiki.USER_RIGHTS_LOG, null, "User:Jimbo Wales", Wiki.ALL_NAMESPACES))
            System.out.println(entry);

        for(String[] result : enWiki.search("WikiProject Spam zola enterprises", Wiki.PROJECT_TALK_NAMESPACE))
            System.out.println(Arrays.toString(result));
 
        System.out.println(enWiki.getSectionText("Wikipedia:Copyright_problems", 2));

        System.out.println(enWiki.parse("{{Main Page}}"));

        // getImage
        byte[] b = enWiki.getImage("GD - Yesterday.jpg");

        System.out.println(enWiki.getTopRevision("Wikipedia:Sandbox"));
        System.out.println(enWiki.getFirstRevision("Wikipedia:Sandbox"));
        System.out.println(enWiki.getFirstRevision("dgfhdfjklg"));
        System.out.println(enWiki.getTopRevision("dgfhdfjklg"));

        // contribs
        for (Wiki.Revision revision : enWiki.contribs("110808020_nilesh"))
            System.out.println(revision);

        // recentchanges
        for (Wiki.Revision revision : enWiki.recentChanges(51))
            System.out.println(revision);

        // getImageHistory
        for (Wiki.LogEntry entry : enWiki.getImageHistory("Davis Motor Car Company logo.jpg"))
            System.out.println(entry);

        // Revision.diff
        System.out.println(enWiki.getRevision(473467375L).diff(Wiki.PREVIOUS_REVISION));

        System.out.println(enWiki.getPageText("Main Page"));
        */
        
        // user exists
        System.out.println(enWiki.userExists("MER-C")); // true
        System.out.println(enWiki.userExists("127.0.0.1")); // false
    }
}
