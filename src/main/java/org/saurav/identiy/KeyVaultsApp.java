package org.saurav.identiy;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.certificates.CertificateClientBuilder;
import com.azure.security.keyvault.certificates.models.CertificatePolicy;
import com.azure.security.keyvault.certificates.models.KeyVaultCertificate;
import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.KeyClientBuilder;
import com.azure.security.keyvault.keys.models.KeyType;
import com.azure.security.keyvault.keys.models.KeyVaultKey;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class KeyVaultsApp {

    private static final String APP_ID = "ffcf709a-9de1-4e33-88bf-b487e53e71b2";
    private static final String TENANT_ID = "f40262c3-5794-45b5-8fbd-bcdba3b6cfec";
    private static final String SECRET = "Qpz8Q~O5_l3bTJLQUxx2FmkFyoS~ocesSN-3-aRN";
    private static final String KEY_VAULT_URL = "https://kv-saurav-az.vault.azure.net/";
    
    private static ClientSecretCredential getClientSecretCredential() {
        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientSecret(SECRET).clientId(APP_ID).tenantId(TENANT_ID).build();
        return credential;
    }
    
    public static void main(String args []) {
        //creataKey();
        //updateKey();
        //deleteKey();
        //createSecret();
        //updateSecret();
        //deleteSecret();
        //createCert();
        //updateCert();
        //deleteCert();
        purgeAll();
    }

    private static void creataKey() {
        KeyClient client = new KeyClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        client.createKey("smp-az-key", KeyType.RSA);
    }

    private static void updateKey() {
        KeyClient client = new KeyClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        KeyVaultKey key = client.getKey("smp-az-key");
        client.updateKeyProperties(key.getProperties().setEnabled(true));
        client.updateKeyProperties(key.getProperties().setExpiresOn(OffsetDateTime.of(2027, 7, 21, 10, 15, 30, 0, ZoneOffset.of("+05:30"))));
    }

    private static void deleteKey() {
        KeyClient client = new KeyClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
       client.beginDeleteKey("smp-az-key");
    }

    private static void createSecret() {
        SecretClient client = new SecretClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        client.setSecret("smp-az-secret","Swati rath");
    }

    private static void updateSecret() {
        SecretClient client = new SecretClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        KeyVaultSecret secret = client.getSecret("smp-az-secret");
        client.updateSecretProperties(secret.getProperties().setEnabled(false));
        client.updateSecretProperties(secret.getProperties().setExpiresOn(OffsetDateTime.of(2029, 3, 11, 10, 15, 30, 0, ZoneOffset.of("+05:30"))));
    }

    private static void deleteSecret() {
        SecretClient client = new SecretClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        client.beginDeleteSecret("smp-az-secret");
    }

    private static void createCert() {
        CertificateClient client = new CertificateClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        CertificatePolicy policy = new CertificatePolicy("self","CN=smpcert.com");
        client.beginCreateCertificate("smp-az-cert",policy);
    }

    private static void updateCert() {
        CertificateClient client = new CertificateClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        KeyVaultCertificate cert = client.getCertificate("smp-az-cert");
        client.updateCertificateProperties(cert.getProperties().setEnabled(false));
    }

    private static void deleteCert() {
        CertificateClient client = new CertificateClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        client.beginDeleteCertificate("smp-az-cert");
    }

    private static void purgeAll() {
        KeyClient keyClient = new KeyClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        SecretClient secretClient = new SecretClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();
        CertificateClient certClient = new CertificateClientBuilder().vaultUrl(KEY_VAULT_URL)
                .credential(KeyVaultsApp.getClientSecretCredential()).buildClient();

        keyClient.purgeDeletedKey("smp-az-key");
        secretClient.purgeDeletedSecret("smp-az-secret");
        certClient.purgeDeletedCertificate("smp-az-cert");
    }


}
