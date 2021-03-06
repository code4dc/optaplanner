/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.investmentallocation.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.investmentallocation.persistence.InvestmentAllocationDao;
import org.optaplanner.examples.investmentallocation.persistence.InvestmentAllocationImporter;
import org.optaplanner.examples.investmentallocation.swingui.InvestmentAllocationPanel;

public class InvestmentAllocationApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/investmentallocation/solver/investmentAllocationSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new InvestmentAllocationApp().init();
    }

    public InvestmentAllocationApp() {
        super("Investment asset class allocation",
                "Decide the percentage of the investor's budget to invest in each asset class.",
                InvestmentAllocationPanel.LOGO_PATH);
    }

    @Override
    protected Solver createSolver() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new InvestmentAllocationPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new InvestmentAllocationDao();
    }

    @Override
    protected AbstractSolutionImporter createSolutionImporter() {
        return new InvestmentAllocationImporter();
    }

}
