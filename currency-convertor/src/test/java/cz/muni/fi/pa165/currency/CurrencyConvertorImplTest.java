package cz.muni.fi.pa165.currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class CurrencyConvertorImplTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency CZK = Currency.getInstance("CZK");

    @Mock
    ExchangeRateTable exchangeRateTable;

    CurrencyConvertor currencyConvertor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        currencyConvertor = new CurrencyConvertorImpl(exchangeRateTable);

    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        // Don't forget to test border values and proper rounding.
        when(exchangeRateTable.getExchangeRate(EUR, CZK)).thenReturn(new BigDecimal("26.25"));

        assertEquals(new BigDecimal("27.54"), currencyConvertor.convert(EUR, CZK, new BigDecimal("1.049")));
        assertEquals(new BigDecimal("27.56"), currencyConvertor.convert(EUR, CZK, new BigDecimal("1.050")));
        assertEquals(new BigDecimal("27.59"), currencyConvertor.convert(EUR, CZK, new BigDecimal("1.051")));
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        exceptionRule.expect(IllegalArgumentException.class);
        currencyConvertor.convert(null, CZK, BigDecimal.TEN);
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        exceptionRule.expect(IllegalArgumentException.class);
        currencyConvertor.convert(CZK, null, BigDecimal.TEN);
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        exceptionRule.expect(IllegalArgumentException.class);
        currencyConvertor.convert(EUR, CZK, null);
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, CZK)).thenReturn(null);
        exceptionRule.expect(UnknownExchangeRateException.class);
        currencyConvertor.convert(EUR, CZK, BigDecimal.TEN);
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, CZK)).thenThrow(UnknownExchangeRateException.class);
        exceptionRule.expect(UnknownExchangeRateException.class);
        currencyConvertor.convert(EUR, CZK, BigDecimal.TEN);
    }

}
