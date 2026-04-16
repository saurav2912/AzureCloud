package org.saurav.managed.identity;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

public class KeyVaultsApp {

    private static final String KEY_VAULT_URL = "https://kv-saurav-az.vault.azure.net/";

    private static DefaultAzureCredential getCredential() {
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder()
                .build();
        return credential;
    }

    public static void main (String [] args) {
        getKVSecret();
    }

    private static void getKVSecret() {
        SecretClient client = new SecretClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getCredential()).buildClient();
        System.out.println(client.getSecret("saurav-az-secret").getValue());
    }
}
