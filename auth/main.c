#include <stdio.h>
#include <time.h>
#include <string.h>
#include <openssl/sha.h>

void hash_time(char *output, size_t output_size) {
    // Get the current timestamp
    time_t rawtime;
    time(&rawtime);
    
    // Write the time to a string
    char time_str[20];
    snprintf(time_str, sizeof(time_str), "%ld", rawtime);

    // Hash it
    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256((unsigned char *)time_str, strlen(time_str), hash);
    
    // Write the hash in hex format
    char hash_hex[SHA256_DIGEST_LENGTH * 2 + 1]; // Sufficient space for hex format
    for (int i = 0; i < SHA256_DIGEST_LENGTH; i++) {
        snprintf(hash_hex + (i * 2), sizeof(hash_hex), "%02x", hash[i]);
    }

    // Create the new key
    size_t hash_len = strlen(hash_hex);
    size_t time_len = strlen(time_str);
    size_t output_index = 0;

    for (size_t i = 0; i < hash_len || i < time_len; i++) {
        if (i < hash_len) {
            output[output_index++] = hash_hex[i]; // Get character from hash
        }
        if (i < time_len) {
            output[output_index++] = time_str[i]; // Get character from timestamp
        }
    }
    
    output[output_index] = '\0'; // Add null terminator
}

int main() {
    char output[SHA256_DIGEST_LENGTH * 2 + 20]; // Sufficient space for hash and timestamp
    hash_time(output, sizeof(output));
    printf("%s\n", output);
    return 0;
}

