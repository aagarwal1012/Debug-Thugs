package debugthugs.mdgiitr.com.greenway;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

class GraphQLCLient {

    public static final String BASE_URL = "https://banku-synfour.herokuapp.com/v1alpha1/graphql";
    private static ApolloClient apolloClient;

    public static ApolloClient getApolloClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient).build();
        return apolloClient;
    }

//    public Void
}
