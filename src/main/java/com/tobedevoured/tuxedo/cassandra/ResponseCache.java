package com.tobedevoured.tuxedo.cassandra;

import com.google.inject.Inject;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;


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
        Keyspace keyspace = context.getEntity();
        MutationBatch mutation = keyspace.prepareMutationBatch();

        mutation.withRow(CF_RESPONSE_CACHE, path)
          .putColumn("response", response, null)
          .putColumn("created_at", System.currentTimeMillis(), null);

         mutation.execute();
    }
}
