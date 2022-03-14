package com.barden.bravo.transaction.http;

import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.transaction.Transaction;
import com.barden.bravo.transaction.provider.TransactionStatisticsProvider;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.scheduler.SchedulerProvider;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Transaction HTTP class.
 */
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionHTTP {

    /**
     * Process transaction.
     *
     * @param json Transactions data bucket.
     * @return Response entity. (JSON OBJECT)
     */
    @PostMapping(value = "/process", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<JsonObject>> process(@RequestBody JsonObject json) {
        DeferredResult<ResponseEntity<JsonObject>> result = new DeferredResult<>();

        //Safety check.
        if (json == null || json.isJsonNull()) {
            result.setResult(new ResponseEntity<>(HTTPResponse.of(false), HttpStatus.OK));
            return result;
        }

        //Process request in a thread to avoid freezing main thread.
        SchedulerProvider.schedule(task -> {
            try {
                //Creates a transaction from json and saves to the database.
                Transaction transaction = Transaction.of(json);
                transaction.save();

                //Statistics.
                TransactionStatisticsProvider.write(transaction);
            } catch (Exception exception) {
                //Responses request to avoid long waiting durations.
                result.setResult(new ResponseEntity<>(HTTPResponse.of(false), HttpStatus.OK));

                //Informs server about the exception. It might be important.
                BardenJavaLibrary.getLogger().error("Couldn't process transaction!", exception);
                return;
            }

            //Sets result.
            result.setResult(new ResponseEntity<>(HTTPResponse.of(true), HttpStatus.OK));
        });

        //Returns response entity.
        return result;
    }
}
