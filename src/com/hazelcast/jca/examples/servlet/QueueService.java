package com.hazelcast.jca.examples.servlet;

import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.jca.HazelcastConnection;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

@Singleton(mappedName = "queueService")
@Startup
@Lock(LockType.READ)
public class QueueService {

    @Resource(name = "HazelcastCF")
    protected ConnectionFactory connectionFactory;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String stage1(String queueName, String message) {
        queueMessage(queueName, message);

        return peekMessage(queueName);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void queueMessage(String queueName, String message) {
        HazelcastConnection c = null;
        try {
            c = (HazelcastConnection) connectionFactory.getConnection();
            TransactionalQueue<String> queue = c.getTransactionalQueue(queueName);

            boolean accepted = false;
            while (!accepted) {
                accepted = queue.offer(message);
            }
        } catch (Exception e) {
            System.out.println("Failed to Queue message");
        } finally {
            closeConnection(c);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String peekMessage(String queueName) {
        HazelcastConnection c = null;
        try {
            c = (HazelcastConnection) connectionFactory.getConnection();
            TransactionalQueue<String> queue = c.getTransactionalQueue(queueName);

            return queue.poll();
        } catch (Exception e) {
            System.out.println("Failed to Queue message");
            return null;
        } finally {
            closeConnection(c);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int size(String queueName) {
        HazelcastConnection c = null;
        try {
            c = (HazelcastConnection) connectionFactory.getConnection();
            Thread.sleep(180_000);
            TransactionalQueue<String> queue = c.getTransactionalQueue(queueName);

            return queue.size();
        } catch (Exception e) {
            System.out.println("Failed to Queue message");
            return 0;
        } finally {
            closeConnection(c);
        }
    }

    private void closeConnection(HazelcastConnection hzConn) {
        if (hzConn != null) {
            try {
                hzConn.close();
            } catch (ResourceException e) {
                throw new RuntimeException(e);
            }
        }
    }
}