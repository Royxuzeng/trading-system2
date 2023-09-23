package algo;

public class RiskWatcher {

    private final int SMA1LARGER = 1;
    private final int SMA2LARGER = 0;
    private int state;

    public RiskWatcher() {
        this.state = -1;
    }


    /**
     * Analyzes and compares the short-term and long-term moving averages
     * to determine whether to issue a buy or sell signal.
     *
     * @param sma1Value - the value of the short-term moving average.
     * @param sma2Value - the value of the long-term moving average.
     */
    public void analyzeAndIssueSignal(double sma1Value, double sma2Value) {
        if (sma1Value != 0 && sma2Value != 0) {
            int newState = determineState(sma1Value, sma2Value);

            if (state != newState) {
                emitSignal(newState);
                state = newState;
            }
        }
    }

    /**
     * Determines the state based on the comparison of short-term and long-term moving averages.
     *
     * @param sma1Value - the value of the short-term moving average.
     * @param sma2Value - the value of the long-term moving average.
     * @return - the determined state.
     */
    private int determineState(double sma1Value, double sma2Value) {
        return sma1Value > sma2Value ? SMA1LARGER : SMA2LARGER;
    }

    /**
     * Emits the buy or sell signal based on the state.
     *
     * @param newState - the state determining the type of signal to be emitted.
     */
    private void emitSignal(int newState) {
        String signalMessage = newState == SMA1LARGER
                ? "The short term moving average crosses above the long term moving average, this indicates a buy signal."
                : "The short term moving average crosses below the long term moving average, it may be a good moment to sell.";
        System.out.println(signalMessage);
    }
}
