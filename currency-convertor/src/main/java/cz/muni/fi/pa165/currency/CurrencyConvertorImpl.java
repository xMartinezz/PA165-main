package cz.muni.fi.pa165.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


/**
 * This is base implementation of {@link CurrencyConvertor}.
 *
 * @author petr.adamek@embedit.cz
 */
public class CurrencyConvertorImpl implements CurrencyConvertor {

    private final ExchangeRateTable exchangeRateTable;
    private final Logger logger = LoggerFactory.getLogger(CurrencyConvertorImpl.class);

    public CurrencyConvertorImpl(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    @Override
    public BigDecimal convert(Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount) {
        logger.trace("convert({},{},{})", sourceCurrency, targetCurrency, sourceAmount);
        if (sourceCurrency == null) {
            throw new IllegalArgumentException("SourceCurrency is null.");
        }
        if (targetCurrency == null) {
            throw new IllegalArgumentException("TargetCurrency is null.");
        }
        if (sourceAmount == null) {
            throw new IllegalArgumentException("SourceAmount is null.");
        }
        try {
            BigDecimal exchangeRate = exchangeRateTable.getExchangeRate(sourceCurrency, targetCurrency);
            if (exchangeRate == null) {
                logger.warn("missing exchange rate from {} to {}", sourceCurrency, targetCurrency);
                throw new UnknownExchangeRateException("Exchange rate unknown.");
            }
            return sourceAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_EVEN);

        } catch (ExternalServiceFailureException e) {
            logger.error("ExternalServiceFailureException: ", e);
            throw new UnknownExchangeRateException("Error while getting exchange rate: ", e);
        }
    }
}
