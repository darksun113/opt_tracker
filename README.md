# opt_tracker
## Usage
```
java -jar opt.jar your_case_number_without_prefix num_of_cases_prior_to_yours num_of_cases_after_yours
```
For example, suppose your case number is YSC1890061234 and you want to know the status of 300 cases before and 100 after:

```
java -jar opt.jar 1890061234 300 100
```

Only supports "YSC" processing center right now. Feel free to change the code to support more.

Output sample:

```
From Fri Dec 01 00:00:00 CST 2017, total 401 cases. 274 are pending(68.33%); 39 are approved(9.73%); 66 are mailed(16.46%); 0 are delivred(0.00%).
Most recent case is YSC1890061264
```

## Future works (May need your help)
1. Supporting more centers.
2. Find a case that is closest to your case.
3. Other analysis.
