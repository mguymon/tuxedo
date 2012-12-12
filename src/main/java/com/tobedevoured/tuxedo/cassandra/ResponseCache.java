package com.tobedevoured.tuxedo.cassandra;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.command.annotation.CommandParam;
import com.google.common.base.Function;
import com.google.inject.Inject;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.recipes.reader.AllRowsReader;
import com.netflix.astyanax.serializers.StringSerializer;


@ByYourCommand
public class ResponseCache {

    public static final  ColumnFamily<String, String> CF_RESPONSE_CACHE =
            new ColumnFamily<String, String>(
              "ResponseCache",              // Column Family Name
              StringSerializer.get(),   // Key Serializer
              StringSerializer.get());  // Column Serializer
    
    private AstyanaxContext<Keyspace> context;
    
    @Inject
    public ResponseCache( AstyanaxContext context ) {
        this.context = context;
    }
    
    @Command(logResult=true)
    @CommandParam(name = "path", type = String.class)
    public String getResponse(String path) throws ConnectionException {
        Keyspace keyspace = context.getEntity();
        OperationResult<ColumnList<String>> result =
                keyspace.prepareQuery(CF_RESPONSE_CACHE)
                  .getKey(path)
                  .execute();
         ColumnList<String> columns = result.getResult();
              
        String response = null;
        if ( columns != null ) {
            Column column = columns.getColumnByName("response");
            if ( column != null ) {
                response = column.getStringValue();
            }
        }
        
        return response;
    }
    
    public void cacheResponse(String path, String response) throws ConnectionException {
        cacheResponse(path, response, null);
    }
    
    public void cacheResponse(String path, String response, Integer ttl ) throws ConnectionException {
        Keyspace keyspace = context.getEntity();
        MutationBatch mutation = keyspace.prepareMutationBatch();

        ColumnListMutation<String> listMutation = mutation.withRow(CF_RESPONSE_CACHE, path)
          .putColumn("response", response, ttl)
          .putColumn("created_at", System.currentTimeMillis(), null);
     
         mutation.execute();
    }
    
    @Command
    @CommandParam(name = "path", type = String.class)
    public void clearCache(String path) throws ConnectionException {
        Keyspace keyspace = context.getEntity();
        MutationBatch mutator = keyspace.prepareMutationBatch();
        mutator.deleteRow((Collection)Arrays.asList(CF_RESPONSE_CACHE), path);
        mutator.execute();
    }
    
    @Command
    public void clearAllCache() throws Exception {
        Keyspace keyspace = context.getEntity();
        keyspace.truncateColumnFamily(CF_RESPONSE_CACHE);
    }
    

    public static void main(String[] args) throws RunException {
        Runner.run( args );
    }
}
