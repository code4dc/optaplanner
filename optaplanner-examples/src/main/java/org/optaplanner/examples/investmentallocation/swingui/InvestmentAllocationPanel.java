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

package org.optaplanner.examples.investmentallocation.swingui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.investmentallocation.domain.AssetClass;
import org.optaplanner.examples.investmentallocation.domain.AssetClassAllocation;
import org.optaplanner.examples.investmentallocation.domain.InvestmentAllocationSolution;
import org.optaplanner.examples.investmentallocation.domain.util.InvestmentAllocationNumericUtil;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class InvestmentAllocationPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/investmentallocation/swingui/investmentAllocationLogo.png";

    private final TimeTablePanel<AssetClass, AssetClass> assetClassPanel;


    public InvestmentAllocationPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        assetClassPanel = new TimeTablePanel<AssetClass, AssetClass>();
        tabbedPane.add("Asset classes", new JScrollPane(assetClassPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private InvestmentAllocationSolution getInvestmentAllocationSolution() {
        return (InvestmentAllocationSolution) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution s) {
        assetClassPanel.reset();
        InvestmentAllocationSolution solution = (InvestmentAllocationSolution) s;
        defineGrid(solution);
        fillCells(solution);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(InvestmentAllocationSolution solution) {
        JButton footprint = new JButton("99999999");
        footprint.setMargin(new Insets(0, 0, 0, 0));
        int footprintWidth = footprint.getPreferredSize().width;

        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_1);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_2);
        assetClassPanel.defineColumnHeaderByKey(HEADER_COLUMN_EXTRA_PROPERTY_3);
        for (AssetClass assetClass : solution.getAssetClassList()) {
            assetClassPanel.defineColumnHeader(assetClass, footprintWidth);
        }

        assetClassPanel.defineRowHeaderByKey(HEADER_ROW_GROUP1);
        assetClassPanel.defineRowHeaderByKey(HEADER_ROW);
        for (AssetClass assetClass : solution.getAssetClassList()) {
            assetClassPanel.defineRowHeader(assetClass);
        }
        assetClassPanel.defineRowHeaderByKey(TRAILING_HEADER_ROW); // Total
    }

    private void fillCells(InvestmentAllocationSolution solution) {
        List<AssetClass> assetClassList = solution.getAssetClassList();
        assetClassPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Asset class"), null));
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, HEADER_ROW,
                createHeaderPanel(new JLabel("Expected return"), null));
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, HEADER_ROW,
                createHeaderPanel(new JLabel("Standard deviation risk"), null));
        assetClassPanel.addColumnHeader(assetClassList.get(0), HEADER_ROW_GROUP1,
                assetClassList.get(assetClassList.size() - 1), HEADER_ROW_GROUP1,
                createHeaderPanel(new JLabel("Correlation"), null));
        JLabel quantityHeaderLabel = new JLabel("Quantity");
        quantityHeaderLabel.setForeground(TangoColorFactory.ORANGE_3);
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_3, HEADER_ROW,
                createHeaderPanel(quantityHeaderLabel, null));
        for (AssetClass assetClass : assetClassList) {
            assetClassPanel.addColumnHeader(assetClass, HEADER_ROW,
                    createHeaderPanel(new JLabel(assetClass.getName(), SwingConstants.CENTER),
                            "Expected return: " + assetClass.getExpectedReturnLabel()
                                    + " - Standard deviation risk: " + assetClass.getStandardDeviationRiskLabel()));
        }
        for (AssetClass assetClass : assetClassList) {
            assetClassPanel.addRowHeader(HEADER_COLUMN, assetClass,
                    createHeaderPanel(new JLabel(assetClass.getName(), SwingConstants.LEFT),
                            "Expected return: " + assetClass.getExpectedReturnLabel()
                                    + " - Standard deviation risk: " + assetClass.getStandardDeviationRiskLabel()));
        }
        for (AssetClass a : assetClassList) {
            for (AssetClass b : assetClassList) {
                assetClassPanel.addCell(a, b, new JLabel(a.getCorrelationLabel(b), SwingConstants.RIGHT));
            }
        }
        assetClassPanel.addCornerHeader(HEADER_COLUMN, TRAILING_HEADER_ROW,
                createHeaderPanel(new JLabel("Total"), null));
        long quantityTotalMillis = 0L;
        for (AssetClassAllocation allocation : solution.getAssetClassAllocationList()) {
            if (allocation.getQuantityMillis() != null) {
                quantityTotalMillis += allocation.getQuantityMillis();
            }
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, allocation.getAssetClass(),
                    new JLabel(allocation.getAssetClass().getExpectedReturnLabel(), SwingConstants.RIGHT));
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, allocation.getAssetClass(),
                    new JLabel(allocation.getAssetClass().getStandardDeviationRiskLabel(), SwingConstants.RIGHT));
            JLabel quantityLabel = new JLabel(allocation.getQuantityLabel(), SwingConstants.RIGHT);
            quantityLabel.setForeground(TangoColorFactory.ORANGE_3);
            assetClassPanel.addRowHeader(HEADER_COLUMN_EXTRA_PROPERTY_3, allocation.getAssetClass(),
                    quantityLabel);
        }
        JLabel expectedReturnLabel = new JLabel(InvestmentAllocationNumericUtil.formatMicrosAsPercentage(solution.calculateExpectedReturnMicros()), SwingConstants.RIGHT);
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_1, TRAILING_HEADER_ROW,
                expectedReturnLabel);
        long standardDeviationMicros = solution.calculateStandardDeviationMicros();
        JLabel standardDeviationLabel = new JLabel(InvestmentAllocationNumericUtil.formatMicrosAsPercentage(standardDeviationMicros), SwingConstants.RIGHT);
        if (standardDeviationMicros > solution.getParametrization().getStandardDeviationMillisMaximum() * 1000L) {
            standardDeviationLabel.setForeground(TangoColorFactory.SCARLET_3);
        }
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_2, TRAILING_HEADER_ROW,
                standardDeviationLabel);
        JLabel quantityTotalLabel = new JLabel(InvestmentAllocationNumericUtil.formatMillisAsPercentage(quantityTotalMillis), SwingConstants.RIGHT);
        quantityTotalLabel.setForeground(TangoColorFactory.ORANGE_3);
        assetClassPanel.addCornerHeader(HEADER_COLUMN_EXTRA_PROPERTY_3, TRAILING_HEADER_ROW, quantityTotalLabel);
    }

    private JPanel createHeaderPanel(JLabel label, String toolTipText) {
        if (toolTipText != null) {
            label.setToolTipText(toolTipText);
        }
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

}
