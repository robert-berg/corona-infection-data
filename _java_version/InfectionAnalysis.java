class InfectionAnalysis {

    private InfectionList il;
    private PopulationData pd;
    private CountryCodes cc;

    public InfectionAnalysis(InfectionList il, PopulationData pd, CountryCodes cc) {
        this.il = il;
        this.pd = pd;
        this.cc = cc;
    }

    public String getCountryISO2() {
        return il.getCountryISO2();
    }

    public int getNumDatapoints() {
        return il.getNumDatapoints();
    }

    public int[] getDailyInfections() {
        return il.getInfections().stream().mapToInt(i -> i).toArray();
    }

    public double[] get7di() {

        int numDatapoints = getNumDatapoints();
        String iso2 = il.getCountryISO2();
        double population = pd.getPopulationForCode(cc.getCodeForISO2(iso2));
        int[] dailyInfections = getDailyInfections();

        double[] data7di = new double[numDatapoints];

        for (int i = 0; i < numDatapoints; i += 1) {

            int data7daySum = 0;

            for (int j = i - 6; j <= i; j += 1) {

                if (j < 0) {
                    continue;
                }
                data7daySum += dailyInfections[j];

            }

            data7di[i] = 100000 * (data7daySum / population);
        }

        return data7di;
    }

}