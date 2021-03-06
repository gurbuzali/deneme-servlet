<%@page import="javax.resource.ResourceException"%>
<%@page import="javax.transaction.*"%>
<%@page import="javax.naming.*"%>
<%@page import="javax.resource.cci.*"%>
<%@page import="java.util.*"%>
<%@page import="com.hazelcast.core.*"%>
<%@page import="com.hazelcast.jca.*"%>

<%
  UserTransaction txn = null;
  HazelcastConnection conn = null;
  HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

  try {
    Context context = new InitialContext();
    txn = (UserTransaction) context.lookup( "java:comp/UserTransaction" );
    txn.begin();

    HazelcastConnectionFactory cf = (HazelcastConnectionFactory)
            context.lookup ( "java:comp/env/HazelcastCF" );

    conn = cf.getConnection();

    TransactionalMap<String, String> txMap = conn.getTransactionalMap( "default" );
    txMap.put( "key", "value" );

    txn.commit();

  } catch ( Throwable e ) {
    if ( txn != null ) {
      try {
        txn.rollback();
      } catch ( Exception ix ) {
        ix.printStackTrace();
      };
    }
    e.printStackTrace();
  } finally {
    if ( conn != null ) {
      try {
        conn.close();
      } catch (Exception ignored) {};
    }
  }
%>