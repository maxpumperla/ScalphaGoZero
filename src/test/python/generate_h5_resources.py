from alphago_zero import *

BASEPATH = "../../main/resources/"

dual_res = dual_residual_network((11, 19, 19))
dual_res.compile(loss=['mse', 'categorical_crossentropy'],optimizer='sgd',metrics=['accuracy'])
dual_res.save(BASEPATH + "dual_res.h5")

sep_res_policy, sep_res_value = separate_residual_network((11, 19, 19))
sep_res_policy.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_res_policy.save(BASEPATH + "sep_res_policy.h5")
sep_res_value.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_res_value.save(BASEPATH + "sep_res_value.h5")

dual_conv = dual_conv_network((11, 19, 19))
dual_conv.compile(loss=['mse', 'categorical_crossentropy'],optimizer='sgd',metrics=['accuracy'])
dual_conv.save(BASEPATH + "dual_conv.h5")

sep_conv_policy, sep_conv_value = separate_conv_network((11, 19, 19))
sep_conv_policy.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_conv_policy.save(BASEPATH + "sep_conv_policy.h5")
sep_conv_value.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_conv_value.save(BASEPATH + "sep_conv_value.h5")