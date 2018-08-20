from alphago_zero import *

dual_res = dual_residual_network((11, 19, 19))
dual_res_json = dual_res.to_json()
with open("../resources/dual_res.json", "w") as f:
    f.write(dual_res_json)

sep_res_policy, sep_res_value = separate_residual_network((11, 19, 19))
sep_res_policy_json = sep_res_policy.to_json()
with open("../resources/sep_res_policy.json", "w") as f:
    f.write(sep_res_policy_json)
sep_res_value_json = sep_res_value.to_json()
with open("../resources/sep_res_value.json", "w") as f:
    f.write(sep_res_value_json)

dual_conv = dual_conv_network((11, 19, 19))
dual_conv_json = dual_conv.to_json()
with open("../resources/dual_conv.json", "w") as f:
    f.write(dual_conv_json)

sep_conv_policy, sep_conv_value = separate_conv_network((11, 19, 19))
sep_conv_policy_json = sep_conv_policy.to_json()
with open("../resources/sep_conv_policy.json", "w") as f:
    f.write(sep_conv_policy_json)
sep_conv_value_json = sep_conv_value.to_json()
with open("../resources/sep_conv_value.json", "w") as f:
    f.write(sep_conv_value_json)
