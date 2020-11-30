package com.example.ibook;

import com.example.ibook.entities.Book;
import com.example.ibook.entities.BookRequest;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationsListUnitTest {

    private BookRequest mockBookRequest() {
        return new BookRequest("RequestUserID", "RequestSenderID", "RequestBookId",
                "Daniel", "MockingBird", "BookID", "Available",
                "2020-01-01", new Date(2020 - 01 - 01));
    }

    private BookRequest mockBookRequest2() {
        return new BookRequest("RequestUserID2", "RequestSenderID2", "RequestBookId2",
                "Saadman", "Physiscs", "BookID", "Available",
                "2020-01-01", new Date(2020 - 20 - 20));
    }


    @Test
    void testAdd() {
        ArrayList<BookRequest> requests = new ArrayList<BookRequest>();
        assertEquals(0,requests.size());
        requests.add(mockBookRequest());
        assertEquals(1,requests.size());
        requests.add(mockBookRequest2());
        assertEquals(2, requests.size());
    }

    @Test
    void testGetRequests() {
        ArrayList<BookRequest> requests = new ArrayList<BookRequest>();
        requests.add(mockBookRequest());
        assertEquals(mockBookRequest().getBookRequestID(), requests.get(0).getBookRequestID());
        assertEquals(mockBookRequest().getDatetime(), requests.get(0).getDatetime());
        assertEquals(mockBookRequest().getRequestedBookID(), requests.get(0).getRequestedBookID());
        assertEquals(mockBookRequest().getRequestedBookTitle(), requests.get(0).getRequestedBookTitle());
        assertEquals(mockBookRequest().getRequestReceiverID(), requests.get(0).getRequestReceiverID());
        assertEquals(mockBookRequest().getRequestSenderID(), requests.get(0).getRequestSenderID());
        assertEquals(mockBookRequest().getBookRequestID(), requests.get(0).getBookRequestID());
        assertEquals(mockBookRequest().getRequestStatus(), requests.get(0).getRequestStatus());
        assertEquals(mockBookRequest().getTimeOfRequest(), requests.get(0).getTimeOfRequest());
        requests.add(mockBookRequest2());
        assertEquals(mockBookRequest2().getBookRequestID(), requests.get(1).getBookRequestID());
        assertEquals(mockBookRequest2().getDatetime(), requests.get(1).getDatetime());
        assertEquals(mockBookRequest2().getRequestedBookID(), requests.get(1).getRequestedBookID());
        assertEquals(mockBookRequest2().getRequestedBookTitle(), requests.get(1).getRequestedBookTitle());
        assertEquals(mockBookRequest2().getRequestReceiverID(), requests.get(1).getRequestReceiverID());
        assertEquals(mockBookRequest2().getRequestSenderID(), requests.get(1).getRequestSenderID());
        assertEquals(mockBookRequest2().getBookRequestID(), requests.get(1).getBookRequestID());
        assertEquals(mockBookRequest2().getRequestStatus(), requests.get(1).getRequestStatus());
        assertEquals(mockBookRequest2().getTimeOfRequest(), requests.get(1).getTimeOfRequest());
    }

    @Test
    void deleteRequests() {
        ArrayList<BookRequest> requests = new ArrayList<BookRequest>();
        assertEquals(0,requests.size());
        requests.add(mockBookRequest());
        assertEquals(1,requests.size());
        requests.add(mockBookRequest2());
        requests.remove(mockBookRequest2());
        assertEquals(false, requests.contains(mockBookRequest2()));
        requests.remove(mockBookRequest());
        assertEquals(false, requests.contains(mockBookRequest()));
    }

}
