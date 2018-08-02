package com.algo.entity;

import com.algo.enumeration.SecurityType;

import java.util.Objects;

/**
 * Base class for all securities.
 */
public class Security {

    private SecurityKey key;
    private String symbol;
     private String currency = "USD";
    private SecurityType securityType = SecurityType.NONE;

    public Security(String symbol, String currency, SecurityType securityType) {
        this.symbol = symbol;
        this.currency = currency;
        this.securityType = securityType;
        this.key = new SecurityKey(this);
    }

    public SecurityKey getKey() {
        return key;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCurrency() {
        return currency;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Security security = (Security) o;
        return Objects.equals(symbol, security.symbol) &&
                Objects.equals(currency, security.currency) &&
                securityType == security.securityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, currency, securityType);
    }

    @Override
    public String toString() {
        return key.toString();
    }

    public static class SecurityKey {
        private final String key;

        public SecurityKey(Security security) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("symbol=").append(security.symbol);
            stringBuilder.append(",currency=").append(security.currency);
            stringBuilder.append(",securityType=").append(security.securityType.toString());
            key = stringBuilder.toString();
        }

        @Override
        public String toString() {
            return key;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object otherKey) {
            if (null == otherKey) {
                return false;
            } else if (otherKey instanceof SecurityKey) {
                return ((SecurityKey) otherKey).key.equals(key);
            } else if (otherKey instanceof String) {
                return otherKey.equals(key);
            }

            return false;
        }
    }
}
