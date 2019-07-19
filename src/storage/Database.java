package storage;

import org.basex.core.*;
import org.basex.core.cmd.*;

public class Database {

    private Context context;

    public Database() throws BaseXException{
        this.context = new Context();
        System.out.println("Creating database");
        new CreateDB("LocalDB", "resources/dblp.xml").execute(context);
        System.out.println("Database was created");
    }

    public String getXMLString() throws BaseXException{
        return new XQuery("<result>{/dblp/*}</result>").execute(context);
    }

    public String getXMLString(String elementOrAttributeName, String value) throws BaseXException{
        String query = "<result>" +
                "{" +
                "/dblp/*[" +
                " exists(./"+elementOrAttributeName+"[contains(lower-case(.),lower-case(\""+value+"\"))]) or " +
                " exists(./.[contains(lower-case(@"+elementOrAttributeName+"),lower-case(\""+value+"\"))]) or " +
                " contains(lower-case(@"+elementOrAttributeName+"),lower-case(\""+value+"\"))" +
                "]" +
                "}" +
                "</result>";

        return new XQuery(query).execute(context);
    }

    public String[] getXMLElements() throws BaseXException{
        String query = "distinct-values(/dblp/*/*/name())";

        return new XQuery(query).execute(context).split(System.lineSeparator());
    }

    public String[] getXMLAttributes() throws BaseXException{
        String query = "distinct-values(/dblp//@*/local-name())";

        return  new XQuery(query).execute(context).split(System.lineSeparator());
    }

}
