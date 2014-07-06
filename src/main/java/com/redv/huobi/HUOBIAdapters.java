package com.redv.huobi;

import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static com.xeiam.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.redv.huobi.dto.marketdata.HUOBIDepth;
import com.redv.huobi.dto.marketdata.HUOBIOrderBookTAS;
import com.redv.huobi.dto.marketdata.HUOBITicker;
import com.redv.huobi.dto.marketdata.HUOBITickerObject;
import com.redv.huobi.dto.marketdata.HUOBITradeObject;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Ticker.TickerBuilder;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.LimitOrder;

/**
 * Various adapters for converting from HUOBI DTOs to XChange DTOs
 */
public final class HUOBIAdapters {

	private HUOBIAdapters() {
	}

	public static Ticker adaptTicker(
			HUOBITicker huobiTicker,
			CurrencyPair currencyPair) {
		HUOBITickerObject ticker = huobiTicker.getTicker();
		return TickerBuilder
			.newInstance()
			.withCurrencyPair(currencyPair)
			.withLast(ticker.getLast())
			.withBid(ticker.getBuy())
			.withAsk(ticker.getSell())
			.withHigh(ticker.getHigh())
			.withLow(ticker.getLow())
			.withVolume(ticker.getVol())
			.build();
	}

	public static OrderBook adaptOrderBook(
			HUOBIDepth huobiDepth,
			CurrencyPair currencyPair) {
		return new OrderBook(null,
			adaptOrderBook(huobiDepth.getAsks(), ASK, currencyPair),
			adaptOrderBook(huobiDepth.getBids(), BID, currencyPair));
	}

	private static List<LimitOrder> adaptOrderBook(
			BigDecimal[][] orders,
			OrderType type,
			CurrencyPair currencyPair) {
		List<LimitOrder> limitOrders = new ArrayList<>(orders.length);
		for (BigDecimal[] order : orders) {
			limitOrders.add(
				new LimitOrder(type, order[1], currencyPair, null, null, order[0]));
		}
		return limitOrders;
	}

	public static Trades adaptTrades(
			HUOBIOrderBookTAS huobiDetail,
			CurrencyPair currencyPair) {
		List<Trade> trades = adaptTrades(huobiDetail.getTrades(), currencyPair);
		return new Trades(trades, SortByTimestamp);
	}

	private static List<Trade> adaptTrades(
			HUOBITradeObject[] trades,
			CurrencyPair currencyPair) {
		List<Trade> tradeList = new ArrayList<>(trades.length);
		for (HUOBITradeObject trade : trades) {
			tradeList.add(adaptTrade(trade, currencyPair));
		}
		return tradeList;
	}

	private static Trade adaptTrade(
			HUOBITradeObject trade,
			CurrencyPair currencyPair) {
		OrderType type = trade.getType().equals("买入") ? BID : ASK;
		final Date time;
		try {
			time = DateUtils.parseDate(trade.getTime(), "HH:mm:ss");
		} catch (ParseException e) {
			throw new ExchangeException(e.getMessage(), e);
		}
		return new Trade(type, trade.getAmount(), currencyPair, trade.getPrice(), time, null);
	}

}
