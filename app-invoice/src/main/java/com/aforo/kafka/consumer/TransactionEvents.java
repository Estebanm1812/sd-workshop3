package com.aforo.kafka.consumer;

import com.aforo.dao.InvoiceDao;
import com.aforo.model.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Objects;
@Service
public class TransactionEvents {

    @Autowired
    private InvoiceDao _dao;

    @Autowired
    private ObjectMapper objectMapper;

    private Logger log = LoggerFactory.getLogger(TransactionEvents.class);

    public void processTransactionEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        Invoice event = objectMapper.readValue(consumerRecord.value(), Invoice.class);
        log.info("Actulizando Invoice ***" + event.getIdInvoice());
        event.setState(1);

   		log.info("Se ha pagado la factura # " + event.getIdInvoice());
        Optional<Invoice> invoiceOptional = _dao.findById(event.getIdInvoice());

        if (invoiceOptional.isPresent()) {
            Invoice invoice = invoiceOptional.get();
            // Update invoice
           // int current = invoice.getAmount();
            boolean flag = Objects.equals(event.getAmount(),invoice.getAmount());
            if(flag){
                invoice.setState(1);

            }
            invoice.setAmount(event.getAmount() - invoice.getAmount());

            // Save invoice
            _dao.save(invoice);
        } else {
            // Save invoice if not exist
            _dao.save(event);
        }
       // _dao.save(event);
    }
}
