package com.chenyumeng.marketplace.order;

import com.chenyumeng.marketplace.order.api.OrderController;
import com.chenyumeng.marketplace.order.domain.Order;
import com.chenyumeng.marketplace.order.domain.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Web-layer slice test: only the controller is loaded, the service is mocked. */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService orderService;

    @Test
    void placesAnOrderAndReturns201WithLocation() throws Exception {
        Order order = new Order(UUID.randomUUID(), "seller-1", "buyer-1", 2500, "EUR",
                Instant.parse("2027-01-04T10:00:00Z"));
        when(orderService.place(any())).thenReturn(order);

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sellerId":"seller-1","buyerId":"buyer-1","amountCents":2500,"currency":"EUR"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/" + order.getId()))
                .andExpect(jsonPath("$.amountCents").value(2500))
                .andExpect(jsonPath("$.status").value("PLACED"));
    }

    @Test
    void rejectsANonPositiveAmount() throws Exception {
        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sellerId":"seller-1","buyerId":"buyer-1","amountCents":0,"currency":"EUR"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderService);
    }

    @Test
    void returns404ForAnUnknownOrder() throws Exception {
        when(orderService.find(any())).thenReturn(Optional.empty());

        mvc.perform(get("/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
