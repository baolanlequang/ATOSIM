import os
import json
import pandas as pd
import numpy as np
import statsmodels.api as sm
from statsmodels.formula.api import ols
import matplotlib.pyplot as plt

# List of all directories to analyze
folders = [
    "lead_stubborn",
    "selfish_lead_stubborn",
    "selfish_trail_stubborn",
    "trail_stubborn"
]

# Map folders to readable titles
friendly_titles = {
    "lead_stubborn": "Lead-Stubborn Mining Strategy",
    "selfish_lead_stubborn": "Combined Selfish + Lead-Stubborn Mining Strategy",
    "selfish_trail_stubborn": "Combined Selfish + Trail-Stubborn Mining Strategy",
    "trail_stubborn": "Trail-Stubborn Mining Strategy"
}

# Parameter friendly names mapping
friendly_names = {
    "num_nodes": "Number of Nodes",
    "node_degree": "Node Degree",
    "prop_delay": "Propagation Delay",
    "block_interval": "Block Interval",
    "max_block_size": "Max Block Size",
    "attacker_hp": "Attacker Hash Power",
    "tie_breaking": "Tie-Breaking Parameter"
}

for folder in folders:
    if not os.path.isdir(folder):
        print(f"Directory '{folder}' not found. Skipping.")
        continue

    print(f"\n======================================================================================")
    print(f"ANALYZING STRATEGY: {friendly_titles[folder].upper()}")
    print(f"======================================================================================")

    run_files = [f for f in os.listdir(folder) if f.startswith("result_run_") and f.endswith(".json")]
    print(f"Loading {len(run_files)} run files from '{folder}'...")

    data_list = []

    for file in run_files:
        filepath = os.path.join(folder, file)
        try:
            with open(filepath, "r") as f:
                run_data = json.load(f)
            
            # Extract input parameters
            input_params = run_data.get("inputParameters", {})
            
            # Map parameters (convert strings to float/int)
            num_nodes = float(input_params.get("validator_count", 0))
            node_degree = float(input_params.get("node_degree", 0))
            prop_delay = float(input_params.get("propagation_delay", 0))
            block_interval = float(input_params.get("block_creation_interval", 0))
            max_block_size = float(input_params.get("max_block_size", 0))
            attacker_hp = float(input_params.get("attacker_hash_power", 0))
            tie_breaking = float(input_params.get("tie_breaking_parameter", 0))
            
            # Extract target: DoubleSpendSuccessProbability average
            ds_success_prob = None
            avg_results = run_data.get("simulationResult", {}).get("averageSimulationRoundResult", [])
            for metric in avg_results:
                if metric.get("name") == "DoubleSpendSuccessProbability":
                    ds_success_prob = float(metric.get("average", 0))
                    break
            
            if ds_success_prob is not None:
                data_list.append({
                    "num_nodes": num_nodes,
                    "node_degree": node_degree,
                    "prop_delay": prop_delay,
                    "block_interval": block_interval,
                    "max_block_size": max_block_size,
                    "attacker_hp": attacker_hp,
                    "tie_breaking": tie_breaking,
                    "success_prob": ds_success_prob
                })
                
        except Exception as e:
            print(f"Error parsing file {file}: {e}")

    df = pd.DataFrame(data_list)
    print(f"Successfully loaded data for {len(df)} runs.")

    # Perform OLS regression and ANOVA
    formula = (
        "success_prob ~ num_nodes + node_degree + prop_delay + "
        "block_interval + max_block_size + attacker_hp + tie_breaking"
    )

    model = ols(formula, data=df).fit()
    anova_table = sm.stats.anova_lm(model, typ=2)

    # Calculate percentage of variance explained (Eta-squared: SS_effect / SS_total)
    ss_total = anova_table["sum_sq"].sum()
    anova_table["eta_sq"] = anova_table["sum_sq"] / ss_total

    # Display results
    print("\nANOVA Table:")
    print("-" * 110)
    print(f"{'Factor':<25} | {'Sum of Squares':<15} | {'df':<5} | {'F-statistic':<12} | {'p-value':<12} | {'Eta-Squared (%)':<15}")
    print("-" * 110)
    for idx, row in anova_table.iterrows():
        p_val_str = f"{row['PR(>F)']:.4e}" if not np.isnan(row["PR(>F)"]) else "N/A"
        f_stat_str = f"{row['F']:.4f}" if not np.isnan(row["F"]) else "N/A"
        eta_sq_str = f"{row['eta_sq']*100:.2f}%"
        print(f"{idx:<25} | {row['sum_sq']:<15.6f} | {int(row['df']):<5} | {f_stat_str:<12} | {p_val_str:<12} | {eta_sq_str:<15}")
    print("-" * 110)

    # Save to CSV
    csv_filename = f"{folder}_anova_results.csv"
    anova_table.to_csv(csv_filename)
    print(f"Saved results to '{csv_filename}'")

    # Generate and save visualization
    plt.figure(figsize=(10, 6))
    factors = anova_table.index[:-1]  # exclude Residuals
    f_values = anova_table["F"][:-1]

    # Sort factors by F-statistic
    sorted_idx = np.argsort(f_values)
    factors_sorted = factors[sorted_idx]
    f_values_sorted = f_values[sorted_idx]

    display_names = [friendly_names.get(name, name) for name in factors_sorted]
    colors = plt.cm.plasma(np.linspace(0.3, 0.8, len(display_names)))

    bars = plt.barh(display_names, f_values_sorted, color=colors, edgecolor='grey', height=0.6)
    plt.xlabel("F-statistic (Effect Size / Importance)", fontsize=12, fontweight='bold')
    plt.title(f"F-ANOVA Feature Importance\n{friendly_titles[folder]}", fontsize=14, fontweight='bold', pad=15)
    plt.grid(axis='x', linestyle='--', alpha=0.5)

    # Add values on the bars
    for bar in bars:
        width = bar.get_width()
        plt.text(width + max(f_values)*0.01 if max(f_values) > 0 else 0.01, 
                 bar.get_y() + bar.get_height()/2, 
                 f'{width:.2f}', 
                 va='center', ha='left', fontsize=10, fontweight='bold')

    plt.tight_layout()
    plot_filename = f"{folder}_anova_feature_importance.png"
    plt.savefig(plot_filename, dpi=300)
    plt.close()
    print(f"Saved plot to '{plot_filename}'")
